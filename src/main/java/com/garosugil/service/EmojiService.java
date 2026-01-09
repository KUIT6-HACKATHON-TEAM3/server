package com.garosugil.service;

import com.garosugil.domain.road.RoadTagLog;
import com.garosugil.domain.user.User;
import com.garosugil.dto.road.RoadTagCreateRequest;
import com.garosugil.repository.RoadTagLogRepository;
import com.garosugil.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmojiService {

    private final RoadTagLogRepository roadTagLogRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createTag(Long segmentId, Long userId, RoadTagCreateRequest request) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 인증이 필요합니다.");
        }

        LocalDate today = LocalDate.now();

        // 오늘 이미 태그를 남겼는지 확인 (created_at의 날짜 부분으로 확인)
        if (roadTagLogRepository.findBySegmentIdAndUserIdAndCreatedAtDate(segmentId, userId, today).isPresent()) {
            throw new IllegalStateException("오늘은 이미 태그를 남기셨어요.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // segment_id가 DB에 없어도 일단 저장 시도
        // (FK 제약조건이 있다면 DB에서 에러가 발생할 수 있지만, 일단 저장 시도)
        RoadTagLog log = RoadTagLog.builder()
                .segmentId(segmentId)
                .user(user)
                .tagCode(request.getTagCode())
                // .visitDate(LocalDate.now())
                .build();

        try {
            roadTagLogRepository.save(log);
        } catch (Exception e) {
            // FK 제약조건 위반 등 DB 에러 처리
            throw new IllegalArgumentException("태그 저장에 실패했습니다: " + e.getMessage(), e);
        }
    }
}
