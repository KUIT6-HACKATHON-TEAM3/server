package com.garosugil.service;

import com.garosugil.domain.music.MusicPlaylist;
import com.garosugil.dto.music.MusicRecommendRequest;
import com.garosugil.dto.music.MusicRecommendResponse;
import com.garosugil.repository.MusicPlaylistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MusicService {

    private final MusicPlaylistRepository musicPlaylistRepository;

    public MusicRecommendResponse recommend(MusicRecommendRequest request) {
        String themeTag = selectThemeTag(request.getWeather(), request.getTimeOfDay());

        MusicPlaylist playlist = musicPlaylistRepository.findFirstByThemeTagIgnoreCase(themeTag)
                .orElseGet(() -> musicPlaylistRepository.findFirstByThemeTagIgnoreCase("DEFAULT").orElse(null));

        if (playlist == null) {
            return new MusicRecommendResponse(
                    "편안한 산책 플레이리스트",
                    "누구나 좋아하는 기본 선곡입니다.",
                    "",
                    null
            );
        }

        return new MusicRecommendResponse(
                playlist.getThemeTitle(),
                playlist.getRecommendReason(),
                playlist.getPlaylistUrl(),
                playlist.getThumbnailUrl()
        );
    }

    private String selectThemeTag(String weather, String timeOfDay) {
        if (timeOfDay != null && !timeOfDay.isBlank()) {
            return timeOfDay.toUpperCase();
        }
        return weather != null ? weather.toUpperCase() : "DEFAULT";
    }
}
