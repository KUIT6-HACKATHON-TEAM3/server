package com.garosugil.domain.road;

import com.garosugil.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "road_likes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoadLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "segment_id", nullable = false)
    private Integer segmentId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public RoadLike(User user, Integer segmentId) {
        this.user = user;
        this.segmentId = segmentId;
    }
}
