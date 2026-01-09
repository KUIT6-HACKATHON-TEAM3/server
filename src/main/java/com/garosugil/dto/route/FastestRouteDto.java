package com.garosugil.dto.route;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FastestRouteDto {
    private String type; // "FASTEST"
    private Integer actual_time;
    private Integer distance_meter;
    private String display_msg;
    private List<PathPoint> path;

    @Getter
    @AllArgsConstructor
    public static class PathPoint {
        private Double lat;
        private Double lng;
    }
}
