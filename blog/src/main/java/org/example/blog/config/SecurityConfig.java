package org.example.blog.config;

import lombok.RequiredArgsConstructor;
import org.example.blog.security.jwt.CustomAuthenticationEntryPoint;
import org.example.blog.security.jwt.JwtAuthenticationFilter;
import org.example.blog.security.jwt.JwtTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration // Spring Security 설정을 위한 클래스임을 명시
@EnableWebSecurity // Spring Security를 사용하기 위한 어노테이션
@RequiredArgsConstructor // 생성자 주입을 위한 Lombok 어노테이션
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final JwtTokenizer jwtTokenizer;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http // HttpSecurity를 사용하여 보안 설정
                // JWT 인증 필터 설정 -> UsernamePasswordAuthenticationFilter 앞에 위치 (우선순위 설정)
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenizer), UsernamePasswordAuthenticationFilter.class)
                // 커스텀 예외 처리 설정
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(customAuthenticationEntryPoint))
                // 요청에 대한 보안 설정
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/", "/loginform", "/login", "userreg", "logout", "userregform", "userreg").permitAll() // 루트 경로, 로그인 페이지, 회원가입 페이지, 로그아웃 페이지는 모두 허용
                                .requestMatchers("/@{username}", "@{username}/**").permitAll() // 사용자 페이지는 모두 허용
                                .requestMatchers("/api/**").permitAll()
                                .anyRequest().authenticated() // 그 외의 요청은 인증된 사용자만 허용
                )
                // CSRF 보안 설정
                .csrf(csrf -> csrf.disable()) // 비활성화 -> 왜? REST API에서 CSRF 토큰을 사용하지 않기 때문
                // 세션 관리 설정
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 사용하지 않음
                // 폼 로그인 설정
                .formLogin(httpSecurityFormLoginConfigurer ->
                        httpSecurityFormLoginConfigurer.disable()) // 폼 로그인 사용하지 않음
                // CORS 설정 (Cross-Origin Resource Sharing)
                .cors(httpSecurityCorsConfigurer ->
                        httpSecurityCorsConfigurer
                                .configurationSource(corsConfigurationSource()))
                // HTTP Basic 설정
                .httpBasic(httpSecurityHttpBasicConfigurer ->
                        httpSecurityHttpBasicConfigurer.disable()); // HTTP Basic 사용하지 않음 -> 왜? REST API에서 사용하지 않기 때문

        return http.build();
    }

    // CORS 설정
    // Cross-Origin Resource Sharing
    // 다른 도메인 간의 리소스 공유를 허용하는 메커니즘
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // URL 기반의 CORS 설정을 위한 객체 생성
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOrigin("*"); // 모든 도메인에 대해 허용
        config.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        config.setAllowedMethods(List.of("GET", "POST", "DELETE", "PATCH", "OPTION", "PUT")); // 허용할 HTTP 메서드 설정

        source.registerCorsConfiguration("/**", config); // 모든 경로에 대해 설정 적용
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 알고리즘을 사용한 PasswordEncoder Bean 등록
    }
}
