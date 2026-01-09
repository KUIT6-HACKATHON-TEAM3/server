package com.garosugil.repository;

import com.garosugil.domain.music.MusicPlaylist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MusicPlaylistRepository extends JpaRepository<MusicPlaylist, Long> {
    Optional<MusicPlaylist> findFirstByThemeTagIgnoreCase(String themeTag);
}
