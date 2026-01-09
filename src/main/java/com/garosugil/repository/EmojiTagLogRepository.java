package com.garosugil.repository;

import com.garosugil.domain.emoji.EmojiTagLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmojiTagLogRepository extends JpaRepository<EmojiTagLog, Long> {
    @Query("SELECT e FROM EmojiTagLog e WHERE e.segmentId = :segmentId AND e.user.id = :userId AND DATE(e.createdAt) = :date")
    Optional<EmojiTagLog> findBySegmentIdAndUserIdAndCreatedAtDate(@Param("segmentId") Long segmentId, @Param("userId") Long userId, @Param("date") LocalDate date);
    
    List<EmojiTagLog> findBySegmentId(Long segmentId);
}
