package org.example.blog.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginResponseDto {
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String name;
}
