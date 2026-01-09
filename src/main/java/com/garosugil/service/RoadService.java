package com.garosugil.service;

import com.garosugil.dto.road.RoadDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 가로수길 상세 정보 서비스
 * 도로명을 조회합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoadService {

    private final RoadDataService roadDataService;

    public RoadDetailResponse getRoadDetail(Integer segmentId, Long currentUserId) {
        // 좋아요 기능은 현재 미지원
        long totalLikeCount = 0;
        boolean isLiked = false;

        // JSON 파일에서 도로명 조회
        String roadName = roadDataService.getRoadName(segmentId);

        return new RoadDetailResponse(segmentId.longValue(), roadName, totalLikeCount, isLiked);
    }
}
