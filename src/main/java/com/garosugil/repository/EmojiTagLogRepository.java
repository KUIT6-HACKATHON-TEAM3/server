package com.garosugil.repository;

import com.garosugil.domain.road.RoadTagLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmojiTagLogRepository extends JpaRepository<RoadTagLog, Long> {
    List<RoadTagLog> findBySegmentId(Long segmentId);
    List<RoadTagLog> findBySegmentIdAndUserId(Long segmentId, Long userId);
}
