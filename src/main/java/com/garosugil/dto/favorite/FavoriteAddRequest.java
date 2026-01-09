package com.garosugil.dto.favorite;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FavoriteAddRequest {

    @NotNull(message = "segment_id는 필수입니다.")
    private Integer segment_id;

    @NotBlank(message = "도로명을 입력해주세요.")
    private String road_name;
}
