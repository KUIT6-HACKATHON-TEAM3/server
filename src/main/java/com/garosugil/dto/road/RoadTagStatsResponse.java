package com.garosugil.dto.road;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RoadTagStatsResponse {
    @Schema(description = "도로 ID", example = "1234")
    private Long roadId;
    
    @Schema(description = "총 태그 수", example = "15")
    private Integer totalCount;
    
    @Schema(description = "내가 선택한 태그 (null 가능)", example = "QUIET")
    private String mySelection; // null 가능
    
    @Schema(description = "태그별 통계 목록")
    private List<RoadTagStatsItem> stats;

    @Getter
    @AllArgsConstructor
    public static class RoadTagStatsItem {
        @Schema(description = "태그 코드", example = "QUIET")
        private String tagCode;
        
        @Schema(description = "태그 라벨", example = "조용함")
        private String label;
        
        @Schema(description = "태그 이모지", example = "🤫")
        private String emoji;
        
        @Schema(description = "해당 태그 선택 수", example = "8")
        private Long count;
        
        @Schema(description = "백분율", example = "53")
        private Integer percentage;
    }
}
