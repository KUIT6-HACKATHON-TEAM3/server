package com.garosugil.dto.route;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AvenueRouteDto {
    @Schema(description = "경로 타입", example = "ECO")
    private String type; // "AVENUE" or "ECO"
    
    @Schema(description = "실제 소요 시간 (분)", example = "15")
    private Integer actual_time;
    
    @Schema(description = "거리 (미터)", example = "1200")
    private Integer distance_meter;
    
    @Schema(description = "표시 메시지", example = "에코 경로")
    private String display_msg;
    
    @Schema(description = "추가된 시간 (분)", example = "3")
    private Integer req_added_time;
    
    @Schema(description = "목표 총 시간 (분)", example = "15")
    private Integer target_total_time;
    
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
