package com.garosugil.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupResponse {
    @Schema(description = "생성된 사용자 ID", example = "1")
    private Long user_id;
    
    @Schema(description = "등록된 이메일", example = "user@example.com")
    private String email;
}
