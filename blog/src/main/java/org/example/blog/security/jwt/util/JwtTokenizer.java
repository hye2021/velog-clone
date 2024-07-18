package org.example.blog.security.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Slf4j
@Component // Spring Bean으로 등록
public class JwtTokenizer {
    private final byte[] accessSecret;
    private final byte[] refreshSecret;

    public final static Long ACCESS_TOKEN_EXPIRE_COUNT = 60 * 60 * 1000L; // an hour
    public final static Long REFRESH_TOKEN_EXPIRE_COUNT = 7 * 24 * 60 * 60 * 1000L; // 7 days

    public JwtTokenizer(@Value("${jwt.secretKey}") String accessSecret,
                        @Value("${jwt.refreshKey}") String refreshSecret) {
        this.accessSecret = accessSecret.getBytes(StandardCharsets.UTF_8);
        this.refreshSecret = refreshSecret.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * AccessToken 생성
     */
    public String createAccessToken(Long id, String email, String name, String username, List<String> roles) {
        return createToken(id, email, name, username, roles, ACCESS_TOKEN_EXPIRE_COUNT, accessSecret);
    }

    /**
     * RefreshToken 생성
     */
    public String createRefreshToken(Long id, String email, String name, String username, List<String> roles) {
        return createToken(id, email, name, username, roles, REFRESH_TOKEN_EXPIRE_COUNT, refreshSecret);
    }

    private String createToken(Long id, String email, String name, String username, List<String> roles, Long expire, byte[] secretKey) {
        // 기본으로 가지고 있는 claim : subject
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", roles);
        claims.put("userId", id);
        claims.put("name", name);
        claims.put("username", username);

        return Jwts.builder() // 빌더 패턴
                .setClaims(claims) // 데이터
                .setIssuedAt(new Date()) // 토큰 발급 시간
                .setExpiration(new Date(new Date().getTime() + expire)) // 만료 시간
                // `signWith` 메서드를 통해 HMAC-SHA 알고리즘을 사용하여 서명
                // `getSigningKey` 메서드를 호출하여 해당 알고리즘에 필요한 SecretKey를 생성
                .signWith(getSigningKey(secretKey)) // 서명
                .compact(); // 직렬화
    }

    /**
     * 토큰에서 유저 아이디 얻기
     */
    public Long getUserIdFromToken(String token) {
        String[] tokenArr = token.split(" ");
        token = tokenArr[1];
        Claims claims = parseToken(token, accessSecret);
        return Long.valueOf((Integer) claims.get("userId"));
    }

    public Claims parseAccessToken(String accessToken) {
        return parseToken(accessToken, accessSecret);
    }

    public Claims parseRefreshToken(String refreshToken) {
        return parseToken(refreshToken, refreshSecret);
    }

    public Claims parseToken(String token, byte[] secretKey) {
        return Jwts.parserBuilder() // 빌더 패턴
                .setSigningKey(getSigningKey(secretKey)) // 서명 키
                .build() // 파서 생성
                .parseClaimsJws(token) // 파싱
                .getBody(); // 데이터(클레임) 반환
    }

    /**
     * [서명키 생성]
     * 주어진 바이트 배열을 기반으로 HMAC-SHA 알고리즘에 필요한 SecretKey 생성
     * @param secretKey - byte형식
     * @return Key 형식 시크릿 키
     */
    public static Key getSigningKey(byte[] secretKey) {
        return Keys.hmacShaKeyFor(secretKey);
    }
}

