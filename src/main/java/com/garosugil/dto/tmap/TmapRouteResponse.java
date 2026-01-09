package com.garosugil.dto.tmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * TMAP 보행자 경로 API 응답 DTO
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmapRouteResponse {
    @JsonProperty("features")
    private List<Feature> features;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Feature {
        @JsonProperty("geometry")
        private Geometry geometry;

        @JsonProperty("properties")
        private Properties properties;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Geometry {
        @JsonProperty("coordinates")
        private List<List<Double>> coordinates; // [[lng, lat], [lng, lat], ...]
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Properties {
        @JsonProperty("totalTime")
        private Integer totalTime; // 초 단위

        @JsonProperty("totalDistance")
        private Integer totalDistance; // 미터 단위

        @JsonProperty("totalFare")
        private Integer totalFare;
    }
}
