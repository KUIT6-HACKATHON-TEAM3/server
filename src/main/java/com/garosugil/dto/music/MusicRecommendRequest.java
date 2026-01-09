package com.garosugil.dto.music;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MusicRecommendRequest {

    @Schema(description = "도로 ID", example = "1234")
    @NotNull(message = "road_id는 필수입니다.")
    private Long road_id;

    @Schema(description = "날씨 정보 (SUNNY: 맑음, CLOUDY: 흐림, RAINY: 비, SNOWY: 눈)", example = "SUNNY")
    @NotNull(message = "날씨 정보는 필수입니다.")
    private String weather; // SUNNY, CLOUDY, RAINY, SNOWY

    @Schema(description = "시간대 정보 (DAY: 낮, SUNSET: 일몽, NIGHT: 밤)", example = "DAY")
    @NotNull(message = "시간대 정보는 필수입니다.")
    private String time_of_day; // DAY, SUNSET, NIGHT
}
