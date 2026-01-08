package com.garosugil.domain.location.repository;

import com.garosugil.domain.location.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    /**
     * 특정 좌표로부터 반경 내의 위치 검색
     * @param longitude 경도
     * @param latitude 위도
     * @param radiusInMeters 반경 (미터)
     * @return 범위 내 위치 목록
     */
    @Query(value = "SELECT * FROM locations l " +
            "WHERE ST_DWithin(" +
            "l.coordinates::geography, " +
            "ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography, " +
            ":radiusInMeters)",
            nativeQuery = true)
    List<Location> findLocationsWithinRadius(
            @Param("longitude") double longitude,
            @Param("latitude") double latitude,
            @Param("radiusInMeters") double radiusInMeters
    );

    /**
     * 이름으로 위치 검색
     */
    List<Location> findByNameContaining(String name);

}
