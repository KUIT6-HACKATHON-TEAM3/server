package com.garosugil.util;

import jakarta.servlet.http.Cookie;

public class CookieUtil {

    /**
     * Access Token 쿠키 생성
     */
    public static Cookie createAccessTokenCookie(String token) {
        Cookie cookie = new Cookie("access_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS에서만 전송
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1시간
        return cookie;
    }

    /**
     * Refresh Token 쿠키 생성
     */
    public static Cookie createRefreshTokenCookie(String token) {
        Cookie cookie = new Cookie("refresh_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS에서만 전송
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7); // 7일
        return cookie;
    }

    /**
     * 쿠키 삭제 (로그아웃용)
     */
    public static Cookie createExpiredCookie(String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 만료
        return cookie;
    }
}
