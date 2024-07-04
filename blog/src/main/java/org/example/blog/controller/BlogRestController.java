package org.example.blog.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.blog.security.dto.UserLoginDto;
import org.example.blog.security.dto.UserLoginResponseDto;
import org.example.blog.entity.RefreshToken;
import org.example.blog.entity.Role;
import org.example.blog.security.jwt.JwtTokenizer;
import org.example.blog.service.RefreshTokenService;
import org.example.blog.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.example.blog.entity.User;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BlogRestController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid UserLoginDto loginDto, // form -> jwt
                                BindingResult bindingResult, // 유효성 검사 결과
                                HttpServletResponse httpServletResponse) {

        // 유효성 검사 실패
        if (bindingResult.hasErrors()) {
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

        return new ResponseEntity(loginResponse, HttpStatus.OK);
    }
}