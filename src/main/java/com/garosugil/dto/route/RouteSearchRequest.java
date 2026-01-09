package com.garosugil.dto.route;

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
    private Location user_location;

    @Schema(description = "목적지 타입 (ROAD_ENTRY: 도로 진입, PIN_COORD: 핀 좌표)", example = "ROAD_ENTRY")
    @NotNull(message = "목적지 타입은 필수입니다.")
    private String target_type; // "ROAD_ENTRY" or "PIN_COORD"

    @Schema(description = "도로 정보 (target_type이 ROAD_ENTRY일 때 사용)")
    @Valid
    private RoadInfo road_info; // target_type이 "ROAD_ENTRY"일 때 사용

    @Schema(description = "핀 위치 (target_type이 PIN_COORD일 때 사용)")
    @Valid
    private Location pin_location; // target_type이 "PIN_COORD"일 때 사용

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

    @Getter
    @lombok.Setter
    @NoArgsConstructor
    @lombok.ToString
    public static class RoadInfo {
        @Schema(description = "도로 시작 좌표")
        @NotNull(message = "시작 좌표는 필수입니다.")
        @Valid
        private Location start;

        @Schema(description = "도로 종료 좌표")
        @NotNull(message = "종료 좌표는 필수입니다.")
        @Valid
        private Location end;
    }
}
