package com.garosugil.dto.favorite;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FavoriteListItem {
    private Long favorite_id;
    private Integer segment_id;
    private String road_name;
    private LocalDateTime created_at;
}
