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
        log.info("경로 탐색 요청: user_location={}, target_type={}",
                request.getUserLocation(), request.getTargetType());

        // 목적지 결정 (API 명세서에 따라)
        RouteSearchRequest.Location start = request.getUserLocation();
        RouteSearchRequest.Location end = determineDestination(request);

        // 1. TMAP API 호출 - 최단 경로
        RouteSearchResponse.RouteInfo fastestRoute = searchFastestRoute(
                start,
                end
        );

        int fastestTimeSec = fastestRoute != null && fastestRoute.getSummary() != null
                ? fastestRoute.getSummary().getDurationSec()
                : 0;

        // 2. 여유 경로 탐색 (기본적으로 10분 추가)
        int reqAddedTimeSec = 600; // 10분 = 600초
        RouteSearchResponse.RouteInfo ecoRoute = searchEcoRoute(
                start,
                end,
                reqAddedTimeSec,
                fastestTimeSec
        );

        if (userId != null && ecoRoute != null) {
            saveNavigationLog(userId, start, end, reqAddedTimeSec / 60, fastestTimeSec / 60, ecoRoute);
        }

        // targetName 결정 (roadInfo가 있으면 도로명, 없으면 "목적지")
        String targetName = "목적지";
        if ("ROAD_ENTRY".equals(request.getTargetType()) && request.getRoadInfo() != null) {
            // TODO: 실제로는 segmentId로 도로명을 조회해야 함
            targetName = "가로수길 진입점";
        }

        // routes 배열 구성
        List<RouteSearchResponse.RouteInfo> routes = new ArrayList<>();
        if (fastestRoute != null) {
            routes.add(fastestRoute);
        }
        if (ecoRoute != null) {
            routes.add(ecoRoute);
        }

        return new RouteSearchResponse(targetName, routes);
    }

    private void saveNavigationLog(Long userId,
                                   RouteSearchRequest.Location start,
                                   RouteSearchRequest.Location end,
                                   int reqAddedTimeMin,
                                   int fastestTimeMin,
                                   RouteSearchResponse.RouteInfo avenueRoute) {
        if (avenueRoute == null || avenueRoute.getSummary() == null) {
            return;
        }

        User user = userRepository.findById(userId).orElse(null);
        int targetTotalTime = fastestTimeMin + reqAddedTimeMin;
        RouteSearchResponse.RouteInfo.Summary summary = avenueRoute.getSummary();

        NavigationLog log = NavigationLog.builder()
                .user(user)
                .startLat(start.getLat())
                .startLng(start.getLng())
                .endLat(end.getLat())
                .endLng(end.getLng())
                .routeType(avenueRoute.getType())
                .addedTimeReq(reqAddedTimeMin)
                .targetTotalTime(targetTotalTime)
                .actualEstTime(summary.getDurationSec() / 60) // 초를 분으로 변환
                .distanceMeters(summary.getDistanceMeter())
                .build();

        navigationLogRepository.save(log);
    }

    private RouteSearchRequest.Location determineDestination(RouteSearchRequest request) {
        if ("PIN_COORD".equals(request.getTargetType()) && request.getPinLocation() != null) {
            return request.getPinLocation();
        }

        if ("ROAD_ENTRY".equals(request.getTargetType()) && request.getRoadInfo() != null) {
            RouteSearchRequest.Location start = request.getRoadInfo().getStart();
            RouteSearchRequest.Location end = request.getRoadInfo().getEnd();
            RouteSearchRequest.Location userLoc = request.getUserLocation();

            double distanceToStart = DistanceUtil.calculateDistance(
                    userLoc.getLat(), userLoc.getLng(),
                    start.getLat(), start.getLng()
            );

            double distanceToEnd = DistanceUtil.calculateDistance(
                    userLoc.getLat(), userLoc.getLng(),
                    end.getLat(), end.getLng()
            );

            return distanceToStart <= distanceToEnd ? start : end;
        }

        throw new IllegalArgumentException("유효하지 않은 목적지 정보입니다.");
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
     * 여유 경로 탐색 (ECO)
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
            
            // tags 추가 (ECO 타입일 때만)
            if (routeInfo != null && "ECO".equals(routeInfo.getType())) {
                List<String> tags = new ArrayList<>();
                tags.add("▲ 그늘 80%");
                tags.add("● 여유로움");
                // tags를 설정하기 위해 새로운 RouteInfo 생성 필요
                // 하지만 현재 구조상 tags는 생성자에 포함되어 있지 않으므로, 
                // AvenueRouteService에서 tags를 포함하도록 수정하거나
                // 여기서 tags를 추가하는 로직이 필요함
                // 일단 기본 구조만 유지
            }
            
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
        List<RouteSearchResponse.RouteInfo.PathPoint> path = new ArrayList<>();
        if (geometry != null && geometry.getCoordinates() != null) {
            for (List<Double> coord : geometry.getCoordinates()) {
                if (coord.size() >= 2) {
                    path.add(new RouteSearchResponse.RouteInfo.PathPoint(
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

        // FASTEST 타입은 tags가 없음
        return new RouteSearchResponse.RouteInfo(type, summary, null, path);
    }
}
