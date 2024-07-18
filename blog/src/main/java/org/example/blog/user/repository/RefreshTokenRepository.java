package org.example.blog.user.repository;

import org.example.blog.user.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByValue(String value); // Optional: null 대신 Optional.empty()를 반환 -> NPE 방지

    Optional<RefreshToken> findAllByUserId(Long userId);
}
