package com.garosugil.controller.route;

import com.garosugil.common.response.ApiResponse;
import com.garosugil.dto.route.RouteSearchRequest;
import com.garosugil.dto.route.RouteSearchResponse;
import com.garosugil.security.auth.UserPrincipal;
import com.garosugil.service.RouteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "🔎 도로 검색 API", description = "도로 검색 관련 API")
@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<RouteSearchResponse>> searchRoutes(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody RouteSearchRequest request) {
        Long userId = userPrincipal != null ? userPrincipal.getUserId() : null;
        RouteSearchResponse response = routeService.searchRoutes(request, userId);
        return ResponseEntity.ok(ApiResponse.success(200, "경로 탐색 완료", response));
    }
}