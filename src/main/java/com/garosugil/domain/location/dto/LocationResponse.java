package com.garosugil.domain.location.dto;

import com.garosugil.domain.location.entity.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationResponse {

    private Long id;
    private String name;
    private String description;
    private Double longitude;
    private Double latitude;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static LocationResponse from(Location location) {
        return LocationResponse.builder()
                .id(location.getId())
                .name(location.getName())
                .description(location.getDescription())
                .longitude(location.getCoordinates().getX())
                .latitude(location.getCoordinates().getY())
                .address(location.getAddress())
                .createdAt(location.getCreatedAt())
                .updatedAt(location.getUpdatedAt())
                .build();
    }

}
