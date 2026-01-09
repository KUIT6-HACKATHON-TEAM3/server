package com.garosugil.dto.route;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RouteSearchResponse {
    private String target_name;
    private List<RouteInfo> routes;

    @Getter
    @AllArgsConstructor
    public static class RouteInfo {
        private String type; // "FASTEST" or "ECO"
        private Summary summary;
        private List<String> tags; // ECO 타입일 때만 포함 (예: ["▲ 그늘 80%", "● 여유로움"])
        private List<PathPoint> path;

        @Getter
        @AllArgsConstructor
        public static class Summary {
            private Integer distance_meter;
            private Integer duration_sec;
        }

        @Getter
        @AllArgsConstructor
        public static class PathPoint {
            private Double lat;
            private Double lng;
        }
    }
}
