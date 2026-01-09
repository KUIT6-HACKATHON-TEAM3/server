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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        RoadTagLog log = RoadTagLog.builder()
                .segmentId(segmentId)
                .user(user)
                .tagCode(request.getTagCode())
                .visitDate(LocalDate.now())
                .build();

        roadTagLogRepository.save(log);
    }
}
