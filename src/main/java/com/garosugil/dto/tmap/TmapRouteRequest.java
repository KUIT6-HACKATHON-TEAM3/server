package com.garosugil.dto.tmap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * TMAP 보행자 경로 API 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TmapRouteRequest {
    private String startX; // 경도
    private String startY; // 위도
    private String endX;   // 경도
    private String endY;   // 위도
    private String reqCoordType; // 요청 좌표계 (WGS84GEO)
    private String resCoordType; // 응답 좌표계 (WGS84GEO)
    private String startName;   // 출발지 이름
    private String endName;     // 도착지 이름
    private String searchOption; // 경로 탐색 옵션 (0: 최단거리, 1: 최소시간)
}
