package com.garosugil.domain.navigation;

import com.garosugil.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "navigation_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NavigationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "start_lat", nullable = false)
    private Double startLat;

    @Column(name = "start_lng", nullable = false)
    private Double startLng;

    @Column(name = "end_lat", nullable = false)
    private Double endLat;

    @Column(name = "end_lng", nullable = false)
    private Double endLng;

    @Column(name = "route_type", nullable = false)
    private String routeType;

    @Column(name = "added_time_req")
    private Integer addedTimeReq;

    @Column(name = "target_total_time")
    private Integer targetTotalTime;

    @Column(name = "actual_est_time")
    private Integer actualEstTime;

    @Column(name = "distance_meters")
    private Integer distanceMeters;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public NavigationLog(User user, Double startLat, Double startLng, Double endLat, Double endLng,
                         String routeType, Integer addedTimeReq, Integer targetTotalTime,
                         Integer actualEstTime, Integer distanceMeters) {
        this.user = user;
        this.startLat = startLat;
        this.startLng = startLng;
        this.endLat = endLat;
        this.endLng = endLng;
        this.routeType = routeType;
        this.addedTimeReq = addedTimeReq;
        this.targetTotalTime = targetTotalTime;
        this.actualEstTime = actualEstTime;
        this.distanceMeters = distanceMeters;
    }
}
