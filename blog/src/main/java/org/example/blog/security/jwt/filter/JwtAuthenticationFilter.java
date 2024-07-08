package org.example.blog.security.jwt.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.blog.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog.security.jwt.exception.JwtExceptionCode;
import org.example.blog.security.jwt.token.JwtAuthenticationToken;
import org.example.blog.security.jwt.util.JwtTokenizer;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


// JWT 토큰을 검증하고 인증 객체를 생성하는 필터
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter { // OncePerRequestFilter: 모든 요청에 대해 단 한 번만 실행되도록 보장
    private final JwtTokenizer jwtTokenizer;

    // 헤더에서 토큰 추출 (Bearer 토큰)
    // Authorization: Bearer {token}
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = getToken(request);
        log.info("*** token : {}", token);

        if(StringUtils.hasText(token)){
            try{
                getAuthentication(token);
            }catch (ExpiredJwtException e){
                log.error("** Expired Token : {}",token,e);
                request.setAttribute("exception", JwtExceptionCode.EXPIRED_TOKEN.getCode());
                throw new BadCredentialsException("Expired token exception", e);
            }catch (UnsupportedJwtException e){
                log.error("** Unsupported Token: {}", token, e);
                request.setAttribute("exception", JwtExceptionCode.UNSUPPORTED_TOKEN.getCode());
                throw new BadCredentialsException("Unsupported token exception", e);
            } catch (MalformedJwtException e) {
                log.error("** Invalid Token: {}", token, e);
                request.setAttribute("exception", JwtExceptionCode.INVALID_TOKEN.getCode());
                throw new BadCredentialsException("Invalid token exception", e);
            } catch (IllegalArgumentException e) {
                log.error("** Token not found: {}", token, e);
                request.setAttribute("exception", JwtExceptionCode.NOT_FOUND_TOKEN.getCode());
                throw new BadCredentialsException("Token not found exception", e);
            } catch (Exception e) {
                log.error("*** JWT Filter - Internal Error: {}", token, e);
                request.setAttribute("exception", JwtExceptionCode.UNKNOWN_ERROR.getCode());
                throw new BadCredentialsException("JWT filter internal exception", e);
            }
        }
        log.info("*** token is not found");
        filterChain.doFilter(request, response); // 다음 필터로 이동
    }

    // 인증 객체 생성 -> SecurityContextHolder에 인증 객체 저장
    private void getAuthentication(String token){
        Claims claims = jwtTokenizer.parseAccessToken(token);
        String email = claims.getSubject();
        Long userId = claims.get("userId", Long.class);
        String name = claims.get("name", String.class);
        String username = claims.get("username", String.class);
        List<GrantedAuthority> authorities = getGrantedAuthorities(claims);

        CustomUserDetails userDetails =
                new CustomUserDetails(username,"",name,authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

        // 인증 객체 생성
        Authentication authentication = new JwtAuthenticationToken(authorities,userDetails,null);
        // SecurityContextHolder에 인증 객체 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // GrantedAuthority : 사용자의 권한을 나타내는 인터페이스
    // 사용자의 role을 GrantedAuthority로 변환하여 List로 반환
    private List<GrantedAuthority> getGrantedAuthorities(Claims claims){
        List<String> roles = (List<String>)claims.get("roles");
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles){
            authorities.add(()->role);
        }
        return authorities;
    }

    // 헤더에서 토큰 추출
    private String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

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

}
