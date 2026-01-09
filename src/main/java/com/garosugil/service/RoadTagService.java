package com.garosugil.service;

import com.garosugil.domain.road.RoadTagLog;
import com.garosugil.domain.user.User;
import com.garosugil.dto.road.RoadTagCreateRequest;
import com.garosugil.dto.road.RoadTagStatsResponse;
import com.garosugil.repository.RoadTagLogRepository;
import com.garosugil.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoadTagService {

    private static final Map<String, String> TAG_LABELS = Map.of(
            "QUIET", "한적해요",
            "NIGHT_VIEW", "야경맛집",
            "DOG", "댕댕이천국",
            "SHADE", "나무그늘",
            "COMFY", "걷기편함",
            "DATE", "데이트코스",
            "PHOTO", "인생샷",
            "SOLO", "혼걸음"
    );

    private static final Map<String, String> TAG_EMOJIS = Map.of(
            "QUIET", "🤫",
            "NIGHT_VIEW", "✨",
            "DOG", "🐶",
            "SHADE", "🌳",
            "COMFY", "🏃",
            "DATE", "💑",
            "PHOTO", "📸",
            "SOLO", "🎧"
    );

    private final RoadTagLogRepository roadTagLogRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createTag(Long roadId, Long userId, RoadTagCreateRequest request) {
        LocalDate today = LocalDate.now();

        if (roadTagLogRepository.findBySegmentIdAndUserIdAndVisitDate(roadId, userId, today).isPresent()) {
            throw new IllegalStateException("오늘은 이미 태그를 남기셨어요.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        RoadTagLog log = RoadTagLog.builder()
                .segmentId(roadId)
                .user(user)
                .tagCode(request.getTagCode())
                .visitDate(today)
                .build();

        roadTagLogRepository.save(log);
    }

    public RoadTagStatsResponse getStats(Long roadId, Long userId) {
        List<RoadTagLog> logs = roadTagLogRepository.findBySegmentId(roadId);

        String mySelection = null;
        if (userId != null) {
            mySelection = roadTagLogRepository.findBySegmentIdAndUserIdAndVisitDate(roadId, userId, LocalDate.now())
                    .map(RoadTagLog::getTagCode)
                    .orElse(null);
        }

        Map<String, Long> counts = logs.stream()
                .collect(Collectors.groupingBy(RoadTagLog::getTagCode, Collectors.counting()));

        int totalCount = logs.size();

        List<RoadTagStatsResponse.RoadTagStatsItem> stats = counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .map(entry -> {
                    Long count = entry.getValue();
                    int percentage = totalCount > 0 ? (int) Math.round((count * 100.0) / totalCount) : 0;
                    return new RoadTagStatsResponse.RoadTagStatsItem(
                            entry.getKey(),
                            TAG_LABELS.getOrDefault(entry.getKey(), entry.getKey()),
                            TAG_EMOJIS.getOrDefault(entry.getKey(), ""),
                            count,
                            percentage
                    );
                })
                .collect(Collectors.toList());

        return new RoadTagStatsResponse(roadId, totalCount, mySelection, stats);
    }
}
