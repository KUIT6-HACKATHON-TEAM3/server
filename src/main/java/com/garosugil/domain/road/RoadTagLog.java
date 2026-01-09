package com.garosugil.domain.road;

import com.garosugil.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "road_tag_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoadTagLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "road_id", nullable = false)
    private Long roadId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "tag_code", nullable = false)
    private String tagCode;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public RoadTagLog(Long roadId, User user, String tagCode, LocalDate visitDate) {
        this.roadId = roadId;
        this.user = user;
        this.tagCode = tagCode;
        this.visitDate = visitDate;
    }
}
