package com.garosugil.dto.route;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RouteSearchRequest {

    @NotNull(message = "사용자 위치는 필수입니다.")
    @Valid
    private Location user_location;

    @NotNull(message = "목적지 타입은 필수입니다.")
    private String target_type; // "ROAD_ENTRY" or "PIN_COORD"

    @Valid
    private RoadInfo road_info; // target_type이 "ROAD_ENTRY"일 때 사용

    @Valid
    private Location pin_location; // target_type이 "PIN_COORD"일 때 사용

    @Getter
    @lombok.Setter
    @NoArgsConstructor
    @lombok.ToString
    public static class Location {
        @NotNull(message = "위도는 필수입니다.")
        private Double lat;

        @NotNull(message = "경도는 필수입니다.")
        private Double lng;
    }

    @Getter
    @lombok.Setter
    @NoArgsConstructor
    @lombok.ToString
    public static class RoadInfo {
        @NotNull(message = "시작 좌표는 필수입니다.")
        @Valid
        private Location start;

        @NotNull(message = "종료 좌표는 필수입니다.")
        @Valid
        private Location end;
    }
}
