package org.example.blog.config;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.blog.service.UserService;

import java.io.IOException;

import static org.example.blog.config.Constants.COOKIE_USER;

public class AuthenticationFilter implements Filter {
    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        System.out.println("[Authentication Filter] " + httpRequest.getRequestURI());
        // System.out.println("[Authentication Filter] " + httpRequest.getMethod());

        // 쿠키에서 userId 추출
        String userId = null;
        Cookie[] cookies = httpRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (COOKIE_USER.equals(cookie.getName())) {
                    userId = cookie.getValue();
                    break;
                }
            }
        }

        // 토큰 검증 및 사용자 ID 추출
        if (userId != null) {
            UserContext.setCurrentUser(userId);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // 필터 종료 시 실행
    }
    
    private String validateTokenAndGetUserId(String token) {
        // 토큰 검증 및 사용자 ID 추출 로직 (예: JWT 검증)
        // 유효한 경우 사용자 ID를 반환, 그렇지 않으면 null 반환

        return null;
    }
}
