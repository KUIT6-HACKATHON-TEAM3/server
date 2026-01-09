package com.garosugil.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 로그인 시 생성되는 토큰 정보 (내부 사용용)
 */
@Getter
@AllArgsConstructor
public class LoginTokens {
    private String accessToken;
    private String refreshToken;
    private String nickname;
}
