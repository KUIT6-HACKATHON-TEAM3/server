package com.garosugil.controller.road;

import com.garosugil.common.response.ApiResponse;
import com.garosugil.dto.road.NearbyRoadResponse;
import com.garosugil.dto.road.RoadDetailResponse;
import com.garosugil.security.auth.UserPrincipal;
import com.garosugil.service.RoadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "🛣️ 가로수길 API", description = "가로수길 관련 API")
@RestController
@RequestMapping("/api/roads")
@RequiredArgsConstructor
public class RoadController {
    private final RoadService roadService;

    @GetMapping("/{segment_id}")
    public ResponseEntity<ApiResponse<RoadDetailResponse>> getRoadDetail(
            @PathVariable("segment_id") Integer segmentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal != null ? userPrincipal.getUserId() : null;
        RoadDetailResponse response = roadService.getRoadDetail(segmentId, userId);
        return ResponseEntity.ok(ApiResponse.success(200, "상세 정보 조회 성공", response));
    }

    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<NearbyRoadResponse>>> getNearbyRoads(
            @RequestParam double minLat,
            @RequestParam double minLng,
            @RequestParam double maxLat,
            @RequestParam double maxLng,
            @RequestParam(required = false) Integer zoomLevel
    ) {
        List<NearbyRoadResponse> list = roadService.getNearbyRoads(minLat, minLng, maxLat, maxLng);
        return ResponseEntity.ok(ApiResponse.success(200, "시각화 데이터 조회 성공", list));
    }
}
