package com.garosugil.dto.road;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoadDetailResponse {
    private Long segment_id;
    private String road_name;
    private Long total_like_count;
    private Boolean is_liked;
}
