package com.garosugil.repository;

import com.garosugil.domain.road.StreetSegment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StreetSegmentRepository extends JpaRepository<StreetSegment, Long> {
}
