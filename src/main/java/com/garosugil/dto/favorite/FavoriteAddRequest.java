package com.garosugil.dto.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FavoriteAddRequest {

    @Schema(description = "관심 장소 별칭", example = "집 근처 카페")
    @NotBlank(message = "별칭은 필수입니다.")
    private String alias;

    @Schema(description = "위도", example = "37.5665")
    @NotNull(message = "위도는 필수입니다.")
    private Double lat;

    @Schema(description = "경도", example = "126.9780")
    @NotNull(message = "경도는 필수입니다.")
    private Double lng;

    @Schema(description = "주소", example = "서울특별시 종로구 세종대로 209")
    @NotBlank(message = "주소는 필수입니다.")
    private String address;
}
