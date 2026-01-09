package com.garosugil.dto.music;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MusicRecommendResponse {
    private String theme_title;
    private String recommend_reason;
    private String playlist_url;
    private String thumbnail_url;
}
