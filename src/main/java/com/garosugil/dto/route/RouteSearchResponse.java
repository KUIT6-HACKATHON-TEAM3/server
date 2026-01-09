package com.garosugil.dto.route;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RouteSearchResponse {
    @Schema(description = "최단 경로 (기준값)")
    private FastestRoute fastest;
    
    @Schema(description = "여유길 (추천값)")
    private AvenueRoute avenue;

    @Getter
    @AllArgsConstructor
    public static class FastestRoute {
        @Schema(description = "경로 타입", example = "FASTEST")
        private String type;
        
        @Schema(description = "실제 소요 시간 (분)", example = "15")
        @JsonProperty("actualTime")
        private Integer actualTime;
        
        @Schema(description = "거리 (미터)", example = "950")
        @JsonProperty("distanceMeter")
        private Integer distanceMeter;
        
        @Schema(description = "경로 좌표 목록")
        private List<PathPoint> path;
    }

    @Getter
    @AllArgsConstructor
    public static class AvenueRoute {
        @Schema(description = "경로 타입", example = "AVENUE")
        private String type;
        
        @Schema(description = "요청한 추가 시간 (분)", example = "25")
        @JsonProperty("reqAddedTime")
        private Integer reqAddedTime;
        
        @Schema(description = "목표 총 시간 (분)", example = "40")
        @JsonProperty("targetTotalTime")
        private Integer targetTotalTime;
        
        @Schema(description = "실제 소요 시간 (분)", example = "38")
        @JsonProperty("actualTime")
        private Integer actualTime;
        
        @Schema(description = "거리 (미터)", example = "2100")
        @JsonProperty("distanceMeter")
        private Integer distanceMeter;
        
        @Schema(description = "경로 좌표 목록")
        private List<PathPoint> path;
    }

    @Getter
    @AllArgsConstructor
    public static class PathPoint {
        @Schema(description = "위도", example = "37.540762")
        private Double lat;
        
        @Schema(description = "경도", example = "127.079342")
        private Double lng;
    }

    // 내부적으로 사용하는 임시 구조 (RouteService에서 사용)
    @Getter
    @AllArgsConstructor
    public static class RouteInfo {
        private String type;
        private Summary summary;
        private List<PathPoint> path;

        @Getter
        @AllArgsConstructor
        public static class Summary {
            @Schema(description = "거리 (미터)", example = "1200")
            private Integer distanceMeter;
            
            @Schema(description = "소요 시간 (초)", example = "720")
            private Integer durationSec;
        }
    }
}
