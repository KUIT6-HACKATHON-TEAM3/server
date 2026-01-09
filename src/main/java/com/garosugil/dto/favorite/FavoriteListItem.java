package com.garosugil.dto.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FavoriteListItem {
    @Schema(description = "관심 길 ID", example = "1")
    private Long favorite_id;
    
    @Schema(description = "도로 세그먼트 ID", example = "1234")
    private Long segment_id;
    
    @Schema(description = "등록 일시", example = "2026-01-09T10:30:00")
    private LocalDateTime created_at;
}
