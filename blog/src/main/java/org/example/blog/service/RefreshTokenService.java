package org.example.blog.service;

import lombok.RequiredArgsConstructor;
import org.example.blog.entity.RefreshToken;
import org.example.blog.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository; // 의존성 주입: final 필드에 대하여 롬복이 자동으로 생성자를 만들어 준다. (RequiredArgsConstructor 필요)

    @Transactional
    public RefreshToken addRefreshToken(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.findByValue(refreshToken).ifPresent(refreshTokenRepository::delete); // ifPresent: Optional 객체가 비어있지 않을 때만 실행
    }

    @Transactional(readOnly = true)
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByValue(token).orElse(null);
    }
}
