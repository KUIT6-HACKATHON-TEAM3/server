package com.garosugil.dto.music;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MusicRecommendResponse {
    @Schema(description = "테마 제목", example = "평화로운 산책길")
    private String theme_title;
    
    @Schema(description = "추천 이유", example = "맑은 날씨에 산책하기 좋은 음악입니다")
    private String recommend_reason;
    
    @Schema(description = "플레이리스트 URL", example = "https://open.spotify.com/playlist/...")
    private String playlist_url;
    
    @Schema(description = "썸네일 이미지 URL", example = "https://i.scdn.co/image/...")
    private String thumbnail_url;
}
