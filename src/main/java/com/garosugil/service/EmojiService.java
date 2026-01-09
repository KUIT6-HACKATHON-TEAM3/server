package com.garosugil.service;

import com.garosugil.domain.emoji.EmojiTagLog;
import com.garosugil.domain.user.User;
import com.garosugil.dto.road.RoadTagCreateRequest;
import com.garosugil.repository.EmojiTagLogRepository;
import com.garosugil.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmojiService {

    private final EmojiTagLogRepository emojiTagLogRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createTag(Long segmentId, Long userId, RoadTagCreateRequest request) {
        LocalDate today = LocalDate.now();

        if (emojiTagLogRepository.findBySegmentIdAndUserIdAndCreatedAtDate(segmentId, userId, today).isPresent()) {
            throw new IllegalStateException("오늘은 이미 태그를 남기셨어요.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        EmojiTagLog log = EmojiTagLog.builder()
                .segmentId(segmentId)
                .user(user)
                .tagCode(request.getTag_code())
                .build();

        emojiTagLogRepository.save(log);
    }
}
