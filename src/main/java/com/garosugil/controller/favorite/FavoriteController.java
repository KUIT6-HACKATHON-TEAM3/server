package com.garosugil.web.favorite;

import com.garosugil.common.response.ApiResponse;
import com.garosugil.dto.favorite.FavoriteAddRequest;
import com.garosugil.dto.favorite.FavoriteAddResponse;
import com.garosugil.dto.favorite.FavoriteListItem;
import com.garosugil.security.auth.UserPrincipal;
import com.garosugil.service.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<ApiResponse<FavoriteAddResponse>> addFavorite(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody FavoriteAddRequest request) {
        FavoriteAddResponse response = favoriteService.addFavorite(
                userPrincipal.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(200, "관심 길에 저장되었습니다.", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FavoriteListItem>>> getFavorites(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<FavoriteListItem> favorites = favoriteService.getFavorites(userPrincipal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(200, "조회 성공", favorites));
    }

    @DeleteMapping("/{favorite_id}")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("favorite_id") Long favoriteId) {
        favoriteService.removeFavorite(userPrincipal.getUserId(), favoriteId);
        return ResponseEntity.ok(ApiResponse.success(200, "관심 길이 삭제되었습니다."));
    }
}

