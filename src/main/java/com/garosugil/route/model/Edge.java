package com.garosugil.route.model;

import com.garosugil.domain.location.dto.RouteSearchRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Edge {
    private final int from;
    private final int to;
    private final EdgeType type;

    // cost / metrics
    private final double costSec;          // 실제 이동 시간(초)
    private final double totalDistanceM;   // 실제 이동 거리(미터)
    private final double rewardAvenueM;    // "가로수길 거리"(WALK=0)

    // AVENUE 메타(응답 구성용)
    private final Integer segmentId; // WALK면 null
    private final String roadName;   // WALK면 null
    private final List<RouteSearchRequest.Location> geometry; // WALK면 null, 방향 맞춘 geometry
}
