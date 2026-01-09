package com.garosugil.repository;

import com.garosugil.domain.road.RoadTagLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoadTagLogRepository extends JpaRepository<RoadTagLog, Long> {
    Optional<RoadTagLog> findBySegmentIdAndUserIdAndVisitDate(Long segmentId, Long userId, LocalDate createdAt);
    List<RoadTagLog> findBySegmentId(Long segmentId);
    List<RoadTagLog> findBySegmentIdAndUserId(Long segmentId, Long userId);
}
