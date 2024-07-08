package org.example.blog.security.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Pattern;

@Getter
@Builder // 빌더 패턴을 사용하기 위한 Lombok 어노테이션
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDto {
    @NotEmpty(message = "username is required")
    private String username;

    @NotEmpty(message = "password is required")
    private String password;
}
