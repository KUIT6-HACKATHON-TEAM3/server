package com.garosugil.dto.road;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RoadTagStatsResponse {
    private Long road_id;
    private Integer total_count;
    private String my_selection; // null 가능
    private List<RoadTagStatsItem> stats;

    @Getter
    @AllArgsConstructor
    public static class RoadTagStatsItem {
        private String tag_code;
        private String label;
        private String emoji;
        private Long count;
        private Integer percentage;
    }
}
