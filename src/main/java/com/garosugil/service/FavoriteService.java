package com.garosugil.service;

import com.garosugil.common.exception.NotFoundException;
import com.garosugil.domain.favorite.Favorite;
import com.garosugil.domain.user.User;
import com.garosugil.dto.favorite.FavoriteAddRequest;
import com.garosugil.dto.favorite.FavoriteAddResponse;
import com.garosugil.dto.favorite.FavoriteListItem;
import com.garosugil.repository.FavoriteRepository;
import com.garosugil.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;

    @Transactional
    public FavoriteAddResponse addFavorite(Long userId, Long segmentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        // 이미 저장된 관심 길인지 확인
        if (favoriteRepository.existsByUserIdAndSegmentId(userId, segmentId)) {
            throw new IllegalArgumentException("이미 등록된 관심 길입니다.");
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .segmentId(segmentId)
                .build();

        Favorite savedFavorite = favoriteRepository.save(favorite);

        return new FavoriteAddResponse(savedFavorite.getId());
    }

    public List<FavoriteListItem> getFavorites(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);

        return favorites.stream()
                .map(favorite -> new FavoriteListItem(
                        favorite.getId(),
                        favorite.getSegmentId(),
                        favorite.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeFavorite(Long userId, Long favoriteId) {
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new NotFoundException("관심 길을 찾을 수 없습니다."));

        if (!favorite.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 관심 길만 삭제할 수 있습니다.");
        }

        favoriteRepository.delete(favorite);
    }
}
