package com.garosugil.controller.emoji;

import com.garosugil.common.response.ApiResponse;
import com.garosugil.dto.road.RoadTagCreateRequest;
import com.garosugil.security.auth.UserPrincipal;
import com.garosugil.service.EmojiService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "😊 이모지 태깅 API", description = "이모지 태그 남기기 관련 API")
@RestController
@RequestMapping("/api/emoji")
@RequiredArgsConstructor
public class EmojiController {
    
    private final EmojiService emojiService;

    @PostMapping("/{segment_id}/tags") // 이모지 태그 남기기 
    public ResponseEntity<ApiResponse<Void>> createTag(
            @PathVariable("segment_id") Long segmentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody RoadTagCreateRequest request) {
        Long userId = userPrincipal != null ? userPrincipal.getUserId() : null;
        emojiService.createTag(segmentId, userId, request);
        return ResponseEntity.status(201).body(ApiResponse.success(201, "태그가 등록되었습니다."));
    }
}
