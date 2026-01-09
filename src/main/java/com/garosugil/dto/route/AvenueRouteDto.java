package com.garosugil.dto.route;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AvenueRouteDto {
    private String type; // "AVENUE" or "ECO"
    private Integer actual_time;
    private Integer distance_meter;
    private String display_msg;
    private Integer req_added_time;
    private Integer target_total_time;
    private List<PathPoint> path;

    @Getter
    @AllArgsConstructor
    public static class PathPoint {
        private Double lat;
        private Double lng;
    }
}
