package com.garosugil.dto.route;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RouteSearchResponse {
    @Schema(description = "목적지 이름", example = "가로수길")
    private String targetName;
    
    @Schema(description = "경로 목록")
    private List<RouteInfo> routes;

    @Getter
    @AllArgsConstructor
    public static class RouteInfo {
        @Schema(description = "경로 타입 (FASTEST: 최단, ECO: 에코)", example = "FASTEST")
        private String type; // "FASTEST" or "ECO"
        
        @Schema(description = "경로 요약 정보")
        private Summary summary;
        
        @Schema(description = "경로 태그 (ECO 타입일 때만 포함)", example = "[▲ 그늘 80%, ● 여유로움]")
        private List<String> tags; // ECO 타입일 때만 포함 (예: ["▲ 그늘 80%", "● 여유로움"])
        
        @Schema(description = "경로 경로 점 목록")
        private List<PathPoint> path;

        @Getter
        @AllArgsConstructor
        public static class Summary {
            @Schema(description = "거리 (미터)", example = "1200")
            private Integer distanceMeter;
            
            @Schema(description = "소요 시간 (초)", example = "720")
            private Integer durationSec;
        }

        @Getter
        @AllArgsConstructor
        public static class PathPoint {
            @Schema(description = "위도", example = "37.5665")
            private Double lat;
            
            @Schema(description = "경도", example = "126.9780")
            private Double lng;
        }
    }
}
