package com.garosugil.repository;

import com.garosugil.domain.favorite.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    boolean existsByUserIdAndAlias(Long userId, String alias);
}
