package com.garosugil.service;

import com.garosugil.dto.route.RouteSearchRequest;
import com.garosugil.dto.route.RouteSearchResponse;
import com.garosugil.dto.tmap.TmapRouteResponse;
import com.garosugil.domain.navigation.NavigationLog;
import com.garosugil.domain.user.User;
import com.garosugil.repository.NavigationLogRepository;
import com.garosugil.repository.UserRepository;
import com.garosugil.util.DistanceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 경로 탐색 서비스
 * TMAP API를 사용하여 보행자 경로를 탐색합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RouteService {

    @Value("${tmap.api.key:}")
    private String tmapApiKey;

    @Value("${tmap.api.url:https://apis.openapi.sk.com/tmap/routes/pedestrian}")
    private String tmapApiUrl;

    private final RestTemplate restTemplate;
    private final AvenueRouteService avenueRouteService;
    private final NavigationLogRepository navigationLogRepository;
    private final UserRepository userRepository;

    public RouteSearchResponse searchRoutes(RouteSearchRequest request, Long userId) {
        log.info("경로 탐색 요청: userLocation={}, pinLocation={}, addedTimeReq={}",
                request.getUserLocation(), request.getPinLocation(), request.getAddedTimeReq());

        // 목적지 결정 (API 명세서에 따라)
        RouteSearchRequest.Location start = request.getUserLocation();
        RouteSearchRequest.Location end = determineDestination(request);

        // 1. TMAP API 호출 - 최단 경로
        RouteSearchResponse.RouteInfo fastestRouteInfo = searchFastestRoute(start, end);

        int fastestTimeSec = fastestRouteInfo != null && fastestRouteInfo.getSummary() != null
                ? fastestRouteInfo.getSummary().getDurationSec()
                : 0;
        int fastestTimeMin = fastestTimeSec / 60;

        // 2. 추가 시간 처리 (null이거나 0이면 기본값 10분)
        int reqAddedTimeMin = (request.getAddedTimeReq() != null && request.getAddedTimeReq() > 0)
                ? request.getAddedTimeReq()
                : 10; // 기본값 10분
        int reqAddedTimeSec = reqAddedTimeMin * 60;

        // 3. 여유 경로 탐색
        RouteSearchResponse.RouteInfo avenueRouteInfo = searchEcoRoute(
                start,
                end,
                reqAddedTimeSec,
                fastestTimeSec
        );

        // 4. Response 객체 생성
        RouteSearchResponse.FastestRoute fastest = null;
        if (fastestRouteInfo != null && fastestRouteInfo.getSummary() != null) {
            fastest = new RouteSearchResponse.FastestRoute(
                    "FASTEST",
                    fastestTimeMin,
                    fastestRouteInfo.getSummary().getDistanceMeter(),
                    fastestRouteInfo.getPath()
            );
        }

        RouteSearchResponse.AvenueRoute avenue = null;
        if (avenueRouteInfo != null && avenueRouteInfo.getSummary() != null) {
            int targetTotalTime = fastestTimeMin + reqAddedTimeMin;
            int actualTimeMin = avenueRouteInfo.getSummary().getDurationSec() / 60;
            
            avenue = new RouteSearchResponse.AvenueRoute(
                    "AVENUE",
                    reqAddedTimeMin,
                    targetTotalTime,
                    actualTimeMin,
                    avenueRouteInfo.getSummary().getDistanceMeter(),
                    avenueRouteInfo.getPath()
            );
        }

        // 5. NavigationLog 저장 (토큰 O: user_id 기록, 토큰 X: user_id NULL)
        if (avenue != null) {
            saveNavigationLog(userId, start, end, reqAddedTimeMin, fastestTimeMin, avenue);
        }

        return new RouteSearchResponse(fastest, avenue);
    }

    private void saveNavigationLog(Long userId,
                                   RouteSearchRequest.Location start,
                                   RouteSearchRequest.Location end,
                                   int reqAddedTimeMin,
                                   int fastestTimeMin,
                                   RouteSearchResponse.AvenueRoute avenueRoute) {
        if (avenueRoute == null) {
            return;
        }

        // userId가 있으면 User 객체 조회, 없으면 null
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        NavigationLog log = NavigationLog.builder()
                .user(user) // null 허용 (토큰 없을 때)
                .startLat(start.getLat())
                .startLng(start.getLng())
                .endLat(end.getLat())
                .endLng(end.getLng())
                .routeType(avenueRoute.getType())
                .addedTimeReq(avenueRoute.getReqAddedTime())
                .targetTotalTime(avenueRoute.getTargetTotalTime())
                .actualEstTime(avenueRoute.getActualTime())
                .distanceMeters(avenueRoute.getDistanceMeter())
                .build();

        navigationLogRepository.save(log);
    }

    private RouteSearchRequest.Location determineDestination(RouteSearchRequest request) {
        // pinLocation이 있으면 그것을 목적지로 사용
        if (request.getPinLocation() != null) {
            return request.getPinLocation();
        }

        throw new IllegalArgumentException("pinLocation은 필수입니다.");
    }

    /**
     * 최단 경로 탐색 (FASTEST)
     */
    private RouteSearchResponse.RouteInfo searchFastestRoute(
            RouteSearchRequest.Location start,
            RouteSearchRequest.Location end) {
        try {
            TmapRouteResponse response = callTmapApi(start, end, "0"); // 0: 최단거리

            if (response != null && response.getFeatures() != null && !response.getFeatures().isEmpty()) {
                return convertTmapResponseToRouteInfo(response, "FASTEST");
            }
        } catch (Exception e) {
            log.error("최단 경로 탐색 실패", e);
        }
        return null;
    }

    /**
     * 여유 경로 탐색 (AVENUE)
     * DFS 알고리즘을 사용하여 가로수길을 최대한 많이 지나는 경로를 탐색합니다.
     */
    private RouteSearchResponse.RouteInfo searchEcoRoute(
            RouteSearchRequest.Location start,
            RouteSearchRequest.Location end,
            int reqAddedTimeSec,
            int fastestTimeSec) {
        try {
            // AvenueRouteService를 사용하여 DFS 기반 여유 경로 탐색
            // reqAddedTime과 fastestTime을 분 단위로 변환
            int reqAddedTimeMin = reqAddedTimeSec / 60;
            int fastestTimeMin = fastestTimeSec / 60;
            RouteSearchResponse.RouteInfo routeInfo = avenueRouteService.searchAvenueRoute(start, end, reqAddedTimeMin, fastestTimeMin);
            return routeInfo;
        } catch (Exception e) {
            log.error("여유 경로 탐색 실패", e);
            return null;
        }
    }

    /**
     * TMAP API 호출
     * 참고: TMAP API는 appKey를 헤더에 넣거나 URL 파라미터로 전달할 수 있습니다.
     * 실제 API 문서에 따라 수정이 필요할 수 있습니다.
     */
    private TmapRouteResponse callTmapApi(
            RouteSearchRequest.Location start,
            RouteSearchRequest.Location end,
            String searchOption) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("appKey", tmapApiKey);

            // TMAP API 요청 형식에 맞게 구성
            // 참고: 실제 TMAP API 문서에 따라 필드명이나 형식이 다를 수 있습니다.
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("startX", String.valueOf(start.getLng()));
            requestBody.put("startY", String.valueOf(start.getLat()));
            requestBody.put("endX", String.valueOf(end.getLng()));
            requestBody.put("endY", String.valueOf(end.getLat()));
            requestBody.put("reqCoordType", "WGS84GEO");
            requestBody.put("resCoordType", "WGS84GEO");
            requestBody.put("startName", "출발지");
            requestBody.put("endName", "도착지");
            requestBody.put("searchOption", searchOption);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<TmapRouteResponse> response = restTemplate.postForEntity(
                    tmapApiUrl,
                    entity,
                    TmapRouteResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("TMAP API 호출 실패: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * TMAP 응답을 RouteInfo로 변환
     */
    private RouteSearchResponse.RouteInfo convertTmapResponseToRouteInfo(
            TmapRouteResponse tmapResponse,
            String type) {
        TmapRouteResponse.Feature feature = tmapResponse.getFeatures().get(0);
        TmapRouteResponse.Properties properties = feature.getProperties();
        TmapRouteResponse.Geometry geometry = feature.getGeometry();

        // 경로 좌표 변환 (TMAP: [lng, lat] -> 우리 형식: {lat, lng})
        List<RouteSearchResponse.PathPoint> path = new ArrayList<>();
        if (geometry != null && geometry.getCoordinates() != null) {
            for (List<Double> coord : geometry.getCoordinates()) {
                if (coord.size() >= 2) {
                    path.add(new RouteSearchResponse.PathPoint(
                            coord.get(1), // lat
                            coord.get(0)  // lng
                    ));
                }
            }
        }

        // Summary 생성 (API 명세서에 따라 distance_meter, duration_sec)
        int durationSec = properties != null ? properties.getTotalTime() : 0;
        int distanceMeter = properties != null ? properties.getTotalDistance() : 0;

        RouteSearchResponse.RouteInfo.Summary summary = new RouteSearchResponse.RouteInfo.Summary(
                distanceMeter,
                durationSec
        );

        return new RouteSearchResponse.RouteInfo(type, summary, path);
    }
}
