package com.garosugil.service;

import com.garosugil.dto.road.RoadDetailResponse;
import com.garosugil.repository.RoadLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 가로수길 상세 정보 서비스
 * 좋아요 정보와 도로명을 조회합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoadService {

    private final RoadLikeRepository roadLikeRepository;
    private final RoadDataService roadDataService;

    public RoadDetailResponse getRoadDetail(Integer segmentId, Long currentUserId) {
        // 전체 좋아요 수 조회
        long totalLikeCount = roadLikeRepository.countBySegmentId(segmentId);

        // 현재 사용자가 좋아요를 눌렀는지 확인
        boolean isLiked = false;
        if (currentUserId != null) {
            isLiked = roadLikeRepository.existsByUserIdAndSegmentId(currentUserId, segmentId);
        }

        // JSON 파일에서 도로명 조회
        String roadName = roadDataService.getRoadName(segmentId);

        return new RoadDetailResponse(segmentId, roadName, totalLikeCount, isLiked);
    }
}
