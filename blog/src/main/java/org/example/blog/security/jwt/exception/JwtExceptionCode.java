package org.example.blog.security.jwt.exception;

import lombok.Getter;
import lombok.Setter;

// JWT 예외 코드와 메시지를 정의한 Enum 클래스
public enum JwtExceptionCode {
    UNKNOWN_ERROR("UNKNOWN_ERROR", "알 수 없는 오류가 발생했습니다."),
    NOT_FOUND_TOKEN("NOT_FOUND_TOKEN", "HEADERS에 토큰 형ㅅ식의 값을 찾을 수 없습니다."),
    INVALID_TOKEN("INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN("EXPIRED_TOKEN", "만료된 토큰입니다"),
    UNSUPPORTED_TOKEN("UNSUPPORTED_TOKEN", "지원되지 않는 토큰입니다.");

    @Getter
    private String code;

    @Setter @Getter
    private String message;

    JwtExceptionCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
