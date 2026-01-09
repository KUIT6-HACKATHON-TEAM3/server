package com.garosugil.repository;

import com.garosugil.domain.road.StreetSegment;
import org.locationtech.jts.geom.LineString;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StreetSegmentRepository extends JpaRepository<StreetSegment, Long> {
    @Query(value = "SELECT * FROM street_segments s WHERE ST_Intersects(s.coordinates, ST_MakeEnvelope(:minLng, :minLat, :maxLng, :maxLat, 4326))", nativeQuery = true)
    List<StreetSegment> findSegmentsInBoundingBox(@Param("minLat") double minLat,
                                                  @Param("minLng") double minLng,
                                                  @Param("maxLat") double maxLat,
                                                  @Param("maxLng") double maxLng);
}
