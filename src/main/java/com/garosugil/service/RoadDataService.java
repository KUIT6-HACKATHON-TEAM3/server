package com.garosugil.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.garosugil.dto.route.RouteSearchRequest;
import lombok.AllArgsConstructor;
import lombok.Setter;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 가로수길 JSON 데이터 로더 서비스
 * segment_id를 기반으로 도로명 및 진입점 정보를 조회합니다.
 */
@Service
@Slf4j
public class RoadDataService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<Integer, RoadInfo> roadInfoMap = new HashMap<>();

    @PostConstruct
    public void loadRoadData() {
        try {
            // JSON 파일을 classpath에서 로드
            // 파일이 src/main/resources/all_roads_walking_paths.json 에 있어야 합니다.
            ClassPathResource resource = new ClassPathResource("all_roads_walking_paths.json");
            
            // 파일이 없으면 루트 디렉토리에서도 시도
            if (!resource.exists()) {
                log.warn("resources 폴더에서 JSON 파일을 찾을 수 없습니다. 루트 디렉토리에서 시도합니다.");
                // 루트 디렉토리에서 로드하는 로직은 필요시 추가
                return;
            }
            
            InputStream inputStream = resource.getInputStream();

            JsonNode rootNode = objectMapper.readTree(inputStream);

            // JSON 구조: [ { "segment_id": 1, "road_name": "...", "locations": { "start": {...}, "end": {...} }, ... } ]
            if (rootNode.isArray()) {
                for (JsonNode item : rootNode) {
                    if (item.has("segment_id")) {
                        Integer segmentId = item.get("segment_id").asInt();
                        String roadName = item.has("road_name") ? item.get("road_name").asText() : "알 수 없는 도로";
                        
                        RoadInfo roadInfo = new RoadInfo();
                        roadInfo.setRoadName(roadName);

                        // locations 정보 파싱
                        if (item.has("locations")) {
                            JsonNode locations = item.get("locations");
                            if (locations.has("start") && locations.has("end")) {
                                JsonNode start = locations.get("start");
                                JsonNode end = locations.get("end");
                                
//                                RoadEntryInfo.Location startLoc = new RoadEntryInfo.Location(
//                                        start.get("lat").asDouble(),
//                                        start.get("lng").asDouble()
//                                );
//                                RoadEntryInfo.Location endLoc = new RoadEntryInfo.Location(
//                                        end.get("lat").asDouble(),
//                                        end.get("lng").asDouble()
//                                );
                                RouteSearchRequest.Location startLoc = new RouteSearchRequest.Location();
                                startLoc.setLat(start.get("lat").asDouble());
                                startLoc.setLng(start.get("lng").asDouble());

                                RouteSearchRequest.Location endLoc = new RouteSearchRequest.Location();
                                endLoc.setLat(end.get("lat").asDouble());
                                endLoc.setLng(end.get("lng").asDouble());

                                roadInfo.setEntryInfo(new RoadEntryInfo(startLoc, endLoc));
                            }
                        }

                        // path_geometry 파싱
                        if (item.has("path_geometry")) {
                            List<RouteSearchRequest.Location> pathGeometry = new ArrayList<>();
                            JsonNode pathGeometryNode = item.get("path_geometry");
                            if (pathGeometryNode.isArray()) {
                                for (JsonNode point : pathGeometryNode) {
                                    if (point.has("lat") && point.has("lng")) {
                                        RouteSearchRequest.Location loc = new RouteSearchRequest.Location();
                                        loc.setLat(point.get("lat").asDouble());
                                        loc.setLng(point.get("lng").asDouble());
                                        pathGeometry.add(loc);
                                    }
                                }
                            }
                            roadInfo.setPathGeometry(pathGeometry);
                        }

                        roadInfoMap.put(segmentId, roadInfo);
                    }
                }
            }

            log.info("가로수길 데이터 로드 완료: {} 개 구간", roadInfoMap.size());
        } catch (Exception e) {
            log.error("가로수길 JSON 파일 로드 실패", e);
        }
    }

    /**
     * segment_id로 도로명 조회
     * @param segmentId 구간 ID
     * @return 도로명 (없으면 "알 수 없는 도로")
     */
    public String getRoadName(Integer segmentId) {
        RoadInfo roadInfo = roadInfoMap.get(segmentId);
        return roadInfo != null ? roadInfo.getRoadName() : "알 수 없는 도로";
    }

    /**
     * segment_id로 진입점 정보 조회 (Start/End 좌표)
     * @param segmentId 구간 ID
     * @return 진입점 정보 (없으면 null)
     */
    public RoadEntryInfo getRoadEntryInfo(Integer segmentId) {
        RoadInfo roadInfo = roadInfoMap.get(segmentId);
        return roadInfo != null ? roadInfo.getEntryInfo() : null;
    }

    /**
     * 모든 가로수길 정보 조회 (DFS 탐색용)
     */
    public List<FullRoadInfo> getAllRoads() {
        List<FullRoadInfo> allRoads = new ArrayList<>();
        for (Map.Entry<Integer, RoadInfo> entry : roadInfoMap.entrySet()) {
            RoadInfo info = entry.getValue();
            if (info.getEntryInfo() != null) {
                allRoads.add(new FullRoadInfo(
                        entry.getKey(),
                        info.getRoadName(),
                        info.getEntryInfo().getStart(),
                        info.getEntryInfo().getEnd(),
                        info.getPathGeometry()
                ));
            }
        }
        return allRoads;
    }

    @Getter
    @lombok.Setter
    private static class RoadInfo {
        private String roadName;
        private RoadEntryInfo entryInfo;
        private List<RouteSearchRequest.Location> pathGeometry;
    }

    /**
     * DFS 탐색용 전체 가로수길 정보
     */
    @Getter
    @AllArgsConstructor
    public static class FullRoadInfo {
        private Integer segmentId;
        private String roadName;
        private RouteSearchRequest.Location start;
        private RouteSearchRequest.Location end;
        private List<RouteSearchRequest.Location> pathGeometry;
    }

    @Getter
    @lombok.AllArgsConstructor
    public static class RoadEntryInfo {
        private RouteSearchRequest.Location start;
        private RouteSearchRequest.Location end;

//        @Getter
//        @lombok.AllArgsConstructor
//        public static class Location {
//            private Double lat;
//            private Double lng;
//        }
    }
}
