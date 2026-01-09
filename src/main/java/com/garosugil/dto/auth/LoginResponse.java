package com.garosugil.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    @Schema(description = "JWT 리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refresh_token;
    
    @Schema(description = "사용자 닉네임", example = "산책러버")
    private String nickname;
}
