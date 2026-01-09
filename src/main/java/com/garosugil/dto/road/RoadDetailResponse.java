package com.garosugil.dto.road;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoadDetailResponse {
    @Schema(description = "도로 세그먼트 ID", example = "1234")
    private Long segment_id;
    
    @Schema(description = "도로명", example = "가로수길")
    private String road_name;
    
    @Schema(description = "총 좋아요 수", example = "42")
    private Long total_like_count;
    
    @Schema(description = "현재 사용자의 좋아요 여부", example = "true")
    private Boolean is_liked;
}
