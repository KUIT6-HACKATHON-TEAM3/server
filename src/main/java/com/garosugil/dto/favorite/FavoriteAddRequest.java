package com.garosugil.dto.favorite;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FavoriteAddRequest {

    @NotBlank(message = "별칭은 필수입니다.")
    private String alias;

    @NotNull(message = "위도는 필수입니다.")
    private Double lat;

    @NotNull(message = "경도는 필수입니다.")
    private Double lng;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;
}
