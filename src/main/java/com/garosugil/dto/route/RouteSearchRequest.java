package com.garosugil.dto.route;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RouteSearchRequest {

    @Schema(description = "사용자 현재 위치")
    @NotNull(message = "사용자 위치는 필수입니다.")
    @Valid
    @JsonProperty("userLocation")
    private Location userLocation;

    @Schema(description = "핀 위치 (목적지 좌표)")
    @Valid
    @JsonProperty("pinLocation")
    private Location pinLocation;

    @Schema(description = "사용자가 더 걷고 싶은 시간(분). 0 또는 null이면 기본값 적용", example = "25")
    @JsonProperty("addedTimeReq")
    private Integer addedTimeReq;

    @Getter
    @lombok.Setter
    @NoArgsConstructor
    @lombok.ToString
    public static class Location {
        @Schema(description = "위도", example = "37.5665")
        @NotNull(message = "위도는 필수입니다.")
        private Double lat;

        @Schema(description = "경도", example = "126.9780")
        @NotNull(message = "경도는 필수입니다.")
        private Double lng;
    }
}
