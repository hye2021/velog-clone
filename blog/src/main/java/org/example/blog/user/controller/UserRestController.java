package org.example.blog.user.controller;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog.user.entity.RefreshToken;
import org.example.blog.user.entity.Role;
import org.example.blog.post.entity.Series;
import org.example.blog.user.entity.User;
import org.example.blog.security.dto.UserLoginDto;
import org.example.blog.security.dto.UserLoginResponseDto;
import org.example.blog.security.jwt.util.JwtTokenizer;
import org.example.blog.user.service.RefreshTokenService;
import org.example.blog.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserRestController {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;

    @GetMapping("/{username}/series")
    public ResponseEntity<List<Series>> getSeriesByUserUsername(@PathVariable(name = "username") String username) {
        User user = userService.getUsersByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Series> series = userService.getSeriesByUser(user.getId());
        return new ResponseEntity<>(series, HttpStatus.OK);
    }

    @GetMapping("/check-auth") // Security Context Holder에 저장된 정보
    public ResponseEntity<Map<String, String>> checkAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, String> response = new HashMap<>();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            String username;

            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }

            log.info("*** [check-auth] {}", username);
            response.put("username", username);
            return ResponseEntity.ok(response);
        }

        log.info("*** [check-auth] Unauthorized");
        response.put("error", "Unauthorized");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    private String getTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid UserLoginDto loginDto, // form -> jwt
                                BindingResult bindingResult, // 유효성 검사 결과
                                HttpServletResponse httpServletResponse) {

        log.info("*** loginDto: {}", loginDto);

        // 유효성 검사 실패
        if (bindingResult.hasErrors()) {
            log.info("*** bindingResult: {}", bindingResult.getAllErrors());
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        // 데이터베이스에서 해당 사용자의 정보를 조회
        User user = userService.getUsersByUsername(loginDto.getUsername());

        // 조회한 정보와 클라이언트가 제출한 비밀번호를 비교하여 인증을 수행
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        // 인증이 성공하면, 사용자의 역할 정보를 포함한 AccessToken과 RefreshToken을 발급
        List<String> roles = user.getRoles().stream().map(Role::getName).toList();

        String accessToken = jwtTokenizer.createAccessToken(user.getId(), user.getEmail(), user.getName(), user.getUsername(), roles);
        String refreshToken = jwtTokenizer.createRefreshToken(user.getId(), user.getEmail(), user.getName(), user.getUsername(), roles);

        // 생성된 RefreshToken을 데이터베이스에 저장
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setValue(refreshToken);
        refreshTokenEntity.setUserId(user.getId());
        refreshTokenService.addRefreshToken(refreshTokenEntity);

        // AccessToken과 RefreshToken을 클라이언트에게 전달
        UserLoginResponseDto loginResponse = UserLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .name(user.getName())
                .build();

        // 각 토큰을 쿠키로 설정
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setMaxAge(Math.toIntExact(jwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000L));

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(Math.toIntExact(jwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT / 1000L));

        httpServletResponse.addCookie(accessTokenCookie);
        httpServletResponse.addCookie(refreshTokenCookie);

        // 클라이언트는 이후 요청에서 AccessToken을 사용하여 인증을 수행할 수 있음

        log.info("*** loginResponse: {}", loginResponse);
        return new ResponseEntity(loginResponse, HttpStatus.OK);
    }

    @PostMapping("/refreshToken") // Access Token이 만료되었을 때, Refresh Token을 사용하여 새로운 Access Token 발급
    public ResponseEntity requestRefresh(HttpServletRequest httpServletRequest,
                                         HttpServletResponse httpServletResponse) {

        String resfreshToken = null;

        // HttpOnly 쿠키에서 Refresh Token을 조회
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    resfreshToken = cookie.getValue();
                    break;
                }
            }
        }

        // Refresh Token이 존재하지 않으면, 401 Unauthorized 반환
        if (resfreshToken == null) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        Claims claims = jwtTokenizer.parseRefreshToken(resfreshToken);
        Long userId = Long.valueOf((Integer)claims.get("userId"));
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        List roles = (List)claims.get("roles");
        String email = (String)claims.get("email");

        String accessToken = jwtTokenizer.createAccessToken(userId, email, user.getName(), user.getUsername(), roles);

        UserLoginResponseDto loginResponse = UserLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(resfreshToken)
                .userId(userId)
                .name(user.getName())
                .build();

        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        // accessTokenCookie.setSecure(true); // HTTPS를 사용하는 경우 true
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000));
        httpServletResponse.addCookie(accessTokenCookie);

        return new ResponseEntity(loginResponse, HttpStatus.OK);
    }

    @GetMapping("/check-username")
    public Map<String, Boolean> checkUsername(@RequestParam("username") String username) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", userService.checkUsername(username));
        return response;
    }

    @GetMapping("/check-email")
    public Map<String, Boolean> checkEmail(@RequestParam("email") String email) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", userService.checkEmail(email));
        return response;
    }
}
