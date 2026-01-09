package com.garosugil.dto.route;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FastestRouteDto {
    @Schema(description = "경로 타입", example = "FASTEST")
    private String type; // "FASTEST"
    
    @Schema(description = "실제 소요 시간 (분)", example = "12")
    private Integer actualTime;
    
    @Schema(description = "거리 (미터)", example = "850")
    private Integer distanceMeter;
    
    @Schema(description = "표시 메시지", example = "최단 경로")
    private String displayMsg;
    
    @Schema(description = "경로 좌표 목록")
    private List<PathPoint> path;

    @Getter
    @AllArgsConstructor
    public static class PathPoint {
        @Schema(description = "위도", example = "37.5665")
        private Double lat;
        
        @Schema(description = "경도", example = "126.9780")
        private Double lng;
    }
}
