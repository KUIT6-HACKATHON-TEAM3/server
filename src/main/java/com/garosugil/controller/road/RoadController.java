package com.garosugil.web.road;

import com.garosugil.common.response.ApiResponse;
import com.garosugil.dto.road.RoadDetailResponse;
import com.garosugil.dto.road.RoadTagCreateRequest;
import com.garosugil.dto.road.RoadTagStatsResponse;
import com.garosugil.security.auth.UserPrincipal;
import com.garosugil.service.RoadService;
import com.garosugil.service.RoadTagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roads")
@RequiredArgsConstructor
public class RoadController {

    private final RoadService roadService;
    private final RoadTagService roadTagService;

    @GetMapping("/{segment_id}")
    public ResponseEntity<ApiResponse<RoadDetailResponse>> getRoadDetail(
            @PathVariable("segment_id") Integer segmentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal != null ? userPrincipal.getUserId() : null;

        RoadDetailResponse response = roadService.getRoadDetail(segmentId, userId);
        return ResponseEntity.ok(ApiResponse.success(200, "상세 정보 조회 성공", response));
    }

    @PostMapping("/{road_id}/tags")
    public ResponseEntity<ApiResponse<Void>> createTag(
            @PathVariable("road_id") Long roadId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody RoadTagCreateRequest request) {
        roadTagService.createTag(roadId, userPrincipal.getUserId(), request);
        return ResponseEntity.status(201).body(ApiResponse.success(201, "태그가 등록되었습니다."));
    }

    @GetMapping("/{road_id}/tags")
    public ResponseEntity<ApiResponse<RoadTagStatsResponse>> getTagStats(
            @PathVariable("road_id") Long roadId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal != null ? userPrincipal.getUserId() : null;
        RoadTagStatsResponse response = roadTagService.getStats(roadId, userId);
        return ResponseEntity.ok(ApiResponse.success(200, "조회 성공", response));
    }
}

