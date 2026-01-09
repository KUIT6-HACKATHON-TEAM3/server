package com.garosugil.repository;

import com.garosugil.domain.road.RoadLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadLikeRepository extends JpaRepository<RoadLike, Long> {
    long countBySegmentId(Integer segmentId);
    boolean existsByUserIdAndSegmentId(Long userId, Integer segmentId);
}
