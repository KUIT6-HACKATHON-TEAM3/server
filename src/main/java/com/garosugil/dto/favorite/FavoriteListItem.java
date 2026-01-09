package com.garosugil.dto.favorite;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FavoriteListItem {
    private Long favorite_id;
    private String alias;
    private Double lat;
    private Double lng;
    private String address;
    private LocalDateTime created_at;
}
