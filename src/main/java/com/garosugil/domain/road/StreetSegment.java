package com.garosugil.domain.road;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;

@Entity
@Table(name = "street_segments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StreetSegment {

    @Id
    @Column(name = "segment_id")
    private Long id;

    @Column(name = "road_name", nullable = false)
    private String roadName;

    @Column(name = "coordinates", columnDefinition = "geometry(LineString,4326)")
    private LineString coordinates;

    @Column(name = "has_trees", nullable = false)
    private Boolean hasTrees;

    @Builder
    public StreetSegment(Long id, String roadName, LineString coordinates, Boolean hasTrees) {
        this.id = id;
        this.roadName = roadName;
        this.coordinates = coordinates;
        this.hasTrees = hasTrees;
    }
}
