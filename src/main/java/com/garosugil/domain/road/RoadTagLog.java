package com.garosugil.domain.road;

import com.garosugil.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import jakarta.persistence.Id;             // (O) 이거여야 합니다!
import jakarta.persistence.GeneratedValue; // (O)
import jakarta.persistence.GenerationType; // (O)
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "road_tag_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoadTagLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "segment_id", nullable = false)
    private Long segmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "tag_code", nullable = false)
    private String tagCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public RoadTagLog(Long segmentId, User user, String tagCode) {
        this.segmentId = segmentId;
        this.user = user;
        this.tagCode = tagCode;
    }
}
