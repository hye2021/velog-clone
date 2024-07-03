package org.example.blog.repository;

import org.example.blog.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByValue(String value); // Optional: null 대신 Optional.empty()를 반환 -> NPE 방지
}
