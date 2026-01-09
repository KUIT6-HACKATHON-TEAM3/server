package com.garosugil.dto.road;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoadTagCreateRequest {
    @Schema(description = "태그 코드 (QUIET: 조용함, NIGHT_VIEW: 야경, TREE: 나무, WALKABLE: 산책하기 좋음, DOG: 반려견, DATE: 데이트, PHOTO: 사진, SOLO: 혼자)", example = "QUIET")
    @NotBlank(message = "태그 코드는 필수입니다.")
    private String tagCode; // QUIET, NIGHT_VIEW, TREE, WALKABLE, DOG, DATE, PHOTO, SOLO
}
