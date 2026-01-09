package com.garosugil.dto.road;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoadTagCreateRequest {

    @NotBlank(message = "태그 코드는 필수입니다.")
    private String tag_code; // QUIET, NIGHT_VIEW, TREE, WALKABLE, DOG, DATE, PHOTO, SOLO
}
