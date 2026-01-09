package com.garosugil.controller.auth;

import com.garosugil.common.response.ApiResponse;
import com.garosugil.dto.auth.LoginRequest;
import com.garosugil.dto.auth.LoginResponse;
import com.garosugil.dto.auth.LoginTokens;
import com.garosugil.dto.auth.MyInfoResponse;
import com.garosugil.dto.auth.ReissueRequest;
import com.garosugil.dto.auth.ReissueResponse;
import com.garosugil.dto.auth.ReissueTokens;
import com.garosugil.dto.auth.SignupRequest;
import com.garosugil.service.AuthService;
import com.garosugil.util.CookieUtil;
import com.garosugil.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "🔐 인증 API", description = "회원가입, 로그인 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
        summary = "회원가입 API", 
        description = "새로운 사용자를 등록합니다. 이메일, 비밀번호, 닉네임이 필요합니다."
    )
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "회원가입이 완료되었습니다.", null));
    }

    @Operation(
        summary = "로그인 API", 
        description = "사용자 인증을 수행하고 액세스 토큰(쿠키)과 리프레시 토큰을 발급합니다."
    )
    @SecurityRequirement(name = "cookieAuth")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse httpResponse) {

        LoginTokens tokens = authService.login(request);

        // Access Token을 쿠키로 설정
        httpResponse.addCookie(CookieUtil.createAccessTokenCookie(tokens.getAccessToken()));
        
        // Response는 refresh token과 nickname만 포함
        LoginResponse response = new LoginResponse(tokens.getRefreshToken(), tokens.getNickname());
        
        return ResponseEntity.ok(ApiResponse.success(200, "로그인 성공", response));
    }

    @Operation(
        summary = "토큰 재발급 API", 
        description = "리프레시 토큰을 사용하여 새로운 액세스 토큰(쿠키)과 리프레시 토큰을 발급합니다."
    )
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<ReissueResponse>> reissue(
            @Valid @RequestBody ReissueRequest request,
            HttpServletResponse httpResponse) {

        ReissueTokens tokens = authService.reissueAccessToken(request);

        // 새로운 Access Token을 쿠키로 설정
        httpResponse.addCookie(CookieUtil.createAccessTokenCookie(tokens.getAccessToken()));
        
        // Response는 새로운 refresh token만 포함
        ReissueResponse response = new ReissueResponse(tokens.getRefreshToken());
        
        return ResponseEntity.ok(ApiResponse.success(200, "토큰 재발급 성공", response));
    }

    @Operation(
        summary = "내 정보 조회 API", 
        description = "현재 로그인한 사용자의 정보를 조회합니다. 액세스 토큰(쿠키)이 필요합니다."
    )
    @SecurityRequirement(name = "cookieAuth")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<MyInfoResponse>> getMyInfo(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        MyInfoResponse response = authService.getMyInfo(userPrincipal.getUserId());
        
        return ResponseEntity.ok(ApiResponse.success(200, "내 정보 조회 성공", response));
    }
}

