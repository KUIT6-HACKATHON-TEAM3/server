package com.garosugil.dto.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FavoriteListItem {
    @Schema(description = "관심 장소 ID", example = "1")
    private Long favorite_id;
    
    @Schema(description = "관심 장소 별칭", example = "집 근처 카페")
    private String alias;
    
    @Schema(description = "위도", example = "37.5665")
    private Double lat;
    
    @Schema(description = "경도", example = "126.9780")
    private Double lng;
    
    @Schema(description = "주소", example = "서울특별시 종로구 세종대로 209")
    private String address;
    
    @Schema(description = "등록 일시", example = "2026-01-09T10:30:00")
    private LocalDateTime created_at;
}
