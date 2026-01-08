package com.garosugil.domain.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationRequest {

    private String name;
    private String description;
    private Double longitude;
    private Double latitude;
    private String address;

}
