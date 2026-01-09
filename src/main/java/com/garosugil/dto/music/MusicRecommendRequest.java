package com.garosugil.dto.music;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MusicRecommendRequest {

    @NotNull(message = "road_id는 필수입니다.")
    private Long road_id;

    @NotNull(message = "날씨 정보는 필수입니다.")
    private String weather; // SUNNY, CLOUDY, RAINY, SNOWY

    @NotNull(message = "시간대 정보는 필수입니다.")
    private String time_of_day; // DAY, SUNSET, NIGHT
}
