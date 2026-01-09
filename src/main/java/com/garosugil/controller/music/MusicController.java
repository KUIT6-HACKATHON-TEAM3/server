package com.garosugil.controller.music;

import com.garosugil.common.response.ApiResponse;
import com.garosugil.dto.music.MusicRecommendRequest;
import com.garosugil.dto.music.MusicRecommendResponse;
import com.garosugil.service.MusicService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "🎶 음악 API", description = "음악 관련 API")
@RestController
@RequestMapping("/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    @PostMapping("/recommend")
    public ResponseEntity<ApiResponse<MusicRecommendResponse>> recommend(
            @Valid @RequestBody MusicRecommendRequest request) {
        MusicRecommendResponse response = musicService.recommend(request);
        return ResponseEntity.ok(ApiResponse.success(200, "추천 완료", response));
    }
}




