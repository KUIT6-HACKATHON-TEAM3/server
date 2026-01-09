package com.garosugil.route;

import com.garosugil.dto.route.RouteSearchRequest;
import com.garosugil.route.model.Edge;
import com.garosugil.route.model.EdgeType;
import com.garosugil.route.model.RouteGraph;
import com.garosugil.service.RoadDataService;
import com.garosugil.util.DistanceUtil;

import java.util.*;

public class AvenueRouteGraphBuilder {

    private final RoadDataService roadDataService;

    private final double radiusKm;          // 예: 3.0
    private final double walkingSpeedMps;   // 예: 1.4
    private final double walkLinkMaxM;      // 예: 500m
    private final int walkLinkTopK;         // 예: 15

    public AvenueRouteGraphBuilder(RoadDataService roadDataService,
                                   double radiusKm,
                                   double walkingSpeedMps,
                                   double walkLinkMaxM,
                                   int walkLinkTopK) {
        this.roadDataService = roadDataService;
        this.radiusKm = radiusKm;
        this.walkingSpeedMps = walkingSpeedMps;
        this.walkLinkMaxM = walkLinkMaxM;
        this.walkLinkTopK = walkLinkTopK;
    }

    public static class BuildResult {
        public final RouteGraph graph;
        public final int startNodeId;
        public final int endNodeId;
        public final int candidateRoadCount;

        public BuildResult(RouteGraph graph, int startNodeId, int endNodeId, int candidateRoadCount) {
            this.graph = graph;
            this.startNodeId = startNodeId;
            this.endNodeId = endNodeId;
            this.candidateRoadCount = candidateRoadCount;
        }
    }

    // 좌표 중복 노드 방지(라운딩)
    private static String key(RouteSearchRequest.Location p) {
        double lat = Math.round(p.getLat() * 1_000_000d) / 1_000_000d;
        double lng = Math.round(p.getLng() * 1_000_000d) / 1_000_000d;
        return lat + "," + lng;
    }

    public BuildResult build(RouteSearchRequest.Location start, RouteSearchRequest.Location end) {
        RouteGraph g = new RouteGraph();

        int startId = g.addNode(start, "START");
        int endId = g.addNode(end, "END");

        // Step 1) 후보 도로: start/end 포함하는 사각형을 radiusKm 만큼 확장한 박스 안의 도로만
        List<RoadDataService.FullRoadInfo> candidates = loadCandidatesInExpandedBox(start, end, radiusKm);

        // 노드 재사용용
        Map<String, Integer> nodeMap = new HashMap<>();
        nodeMap.put(key(start), startId);
        nodeMap.put(key(end), endId);

        // Step 2) AVENUE 엣지 추가 (도로 endpoint 노드 생성 + 양방향)
        for (RoadDataService.FullRoadInfo road : candidates) {
            int a = nodeMap.computeIfAbsent(key(road.getStart()),
                    k -> g.addNode(road.getStart(), "R:" + road.getSegmentId() + ":S"));
            int b = nodeMap.computeIfAbsent(key(road.getEnd()),
                    k -> g.addNode(road.getEnd(), "R:" + road.getSegmentId() + ":E"));

            double roadDistance = calcRoadDistance(road);
            double roadTime = roadDistance / walkingSpeedMps;

            List<RouteSearchRequest.Location> geomForward = road.getPathGeometry();
            List<RouteSearchRequest.Location> geomBackward = (geomForward == null) ? null : reverseCopy(geomForward);

            g.addEdge(new Edge(a, b, EdgeType.AVENUE, roadTime, roadDistance, roadDistance,
                    road.getSegmentId(), road.getRoadName(), geomForward));

            g.addEdge(new Edge(b, a, EdgeType.AVENUE, roadTime, roadDistance, roadDistance,
                    road.getSegmentId(), road.getRoadName(), geomBackward));
        }

        // Step 3) WALK 엣지 구성 (모든 노드 무연결 방지 + 폭발 방지 위해 topK & maxM 제한)
        addWalkEdges(g);

        return new BuildResult(g, startId, endId, candidates.size());
    }

    private List<RoadDataService.FullRoadInfo> loadCandidatesInExpandedBox(
            RouteSearchRequest.Location start,
            RouteSearchRequest.Location end,
            double radiusKm
    ) {
        double minLat = Math.min(start.getLat(), end.getLat());
        double maxLat = Math.max(start.getLat(), end.getLat());
        double minLng = Math.min(start.getLng(), end.getLng());
        double maxLng = Math.max(start.getLng(), end.getLng());

        // 대략 변환 (정밀 지리계산 아님, 후보 필터 목적)
        double avgLatRad = Math.toRadians((start.getLat() + end.getLat()) / 2.0);
        double latDelta = radiusKm / 111.0; // 1도 ~ 111km
        double lngDelta = radiusKm / (111.0 * Math.cos(avgLatRad));

        double boxMinLat = minLat - latDelta;
        double boxMaxLat = maxLat + latDelta;
        double boxMinLng = minLng - lngDelta;
        double boxMaxLng = maxLng + lngDelta;

        List<RoadDataService.FullRoadInfo> all = roadDataService.getAllRoads();
        List<RoadDataService.FullRoadInfo> out = new ArrayList<>();

        for (RoadDataService.FullRoadInfo r : all) {
            if (inBox(r.getStart(), boxMinLat, boxMaxLat, boxMinLng, boxMaxLng) ||
                    inBox(r.getEnd(),   boxMinLat, boxMaxLat, boxMinLng, boxMaxLng)) {
                out.add(r);
            }
        }

        return out;
    }

    private boolean inBox(RouteSearchRequest.Location p,
                          double minLat, double maxLat, double minLng, double maxLng) {
        return p.getLat() >= minLat && p.getLat() <= maxLat &&
                p.getLng() >= minLng && p.getLng() <= maxLng;
    }

    private void addWalkEdges(RouteGraph g) {
        int n = g.getNodes().size();
        if (n <= 1) return;

        // 노드간 거리 행렬 1회 계산 (기존 DFS처럼 정렬 과정에서 거리 계산 반복 방지)
        double[][] dist = new double[n][n];
        for (int i = 0; i < n; i++) {
            var pi = g.getNodes().get(i).getLoc();
            for (int j = i + 1; j < n; j++) {
                var pj = g.getNodes().get(j).getLoc();
                double d = DistanceUtil.calculateDistance(pi.getLat(), pi.getLng(), pj.getLat(), pj.getLng());
                dist[i][j] = d;
                dist[j][i] = d;
            }
        }

        for (int i = 0; i < n; i++) {
            List<Integer> neighbors = new ArrayList<>();
            for (int j = 0; j < n; j++) if (j != i) neighbors.add(j);

            int finalI = i;
            neighbors.sort(Comparator.comparingDouble(j -> dist[finalI][j]));

            int added = 0;
            for (int j : neighbors) {
                double d = dist[i][j];
                if (d > walkLinkMaxM) break;

                double t = d / walkingSpeedMps;
                g.addEdge(new Edge(i, j, EdgeType.WALK, t, d, 0.0,
                        null, null, null));

                added++;
                if (added >= walkLinkTopK) break;
            }
        }
    }

    private double calcRoadDistance(RoadDataService.FullRoadInfo road) {
        var geom = road.getPathGeometry();
        if (geom == null || geom.size() < 2) {
            return DistanceUtil.calculateDistance(
                    road.getStart().getLat(), road.getStart().getLng(),
                    road.getEnd().getLat(), road.getEnd().getLng()
            );
        }
        double sum = 0;
        for (int i = 0; i < geom.size() - 1; i++) {
            var a = geom.get(i);
            var b = geom.get(i + 1);
            sum += DistanceUtil.calculateDistance(a.getLat(), a.getLng(), b.getLat(), b.getLng());
        }
        return sum;
    }

    private List<RouteSearchRequest.Location> reverseCopy(List<RouteSearchRequest.Location> src) {
        List<RouteSearchRequest.Location> out = new ArrayList<>(src);
        Collections.reverse(out);
        return out;
    }
}
