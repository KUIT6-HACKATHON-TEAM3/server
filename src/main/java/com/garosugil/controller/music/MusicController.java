package com.garosugil.web.music;

import com.garosugil.common.response.ApiResponse;
import com.garosugil.dto.music.MusicRecommendRequest;
import com.garosugil.dto.music.MusicRecommendResponse;
import com.garosugil.service.MusicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/music")
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




