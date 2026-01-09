package com.garosugil.controller.route;

import com.garosugil.common.response.ApiResponse;
import com.garosugil.dto.route.RouteSearchRequest;
import com.garosugil.dto.route.RouteSearchResponse;
import com.garosugil.security.auth.UserPrincipal;
import com.garosugil.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.ok(ApiResponse.success(200, "경로 탐색 성공", response));
    }
}

