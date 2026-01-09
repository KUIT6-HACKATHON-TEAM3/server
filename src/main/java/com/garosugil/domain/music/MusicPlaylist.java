package com.garosugil.domain.music;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "music_playlists")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MusicPlaylist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "theme_tag", nullable = false)
    private String themeTag;

    @Column(name = "theme_title", nullable = false)
    private String themeTitle;

    @Column(name = "recommend_reason", nullable = false)
    private String recommendReason;

    @Column(name = "playlist_url", nullable = false)
    private String playlistUrl;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
}
