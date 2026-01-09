package com.garosugil.dto.favorite;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FavoriteAddRequest {

    @Schema(description = "도로 세그먼트 ID", example = "1234")
    @NotNull(message = "segment_id는 필수입니다.")
    @JsonProperty("segment_id")
    private Long segmentId;
}
