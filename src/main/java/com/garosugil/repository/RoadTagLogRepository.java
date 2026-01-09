package com.garosugil.repository;

import com.garosugil.domain.road.RoadTagLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoadTagLogRepository extends JpaRepository<RoadTagLog, Long> {
    @Query("SELECT r FROM RoadTagLog r WHERE r.segmentId = :segmentId AND r.user.id = :userId AND DATE(r.createdAt) = :date")
    Optional<RoadTagLog> findBySegmentIdAndUserIdAndCreatedAtDate(@Param("segmentId") Long segmentId, @Param("userId") Long userId, @Param("date") LocalDate date);
    
    List<RoadTagLog> findBySegmentId(Long segmentId);
    List<RoadTagLog> findBySegmentIdAndUserId(Long segmentId, Long userId);
}
