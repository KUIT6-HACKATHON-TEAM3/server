package com.garosugil.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyInfoResponse {
    @Schema(description = "사용자 닉네임", example = "산책러버")
    private String nickname;
    
    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String email;
}
