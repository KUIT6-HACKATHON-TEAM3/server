package com.garosugil.service;

import com.garosugil.dto.route.RouteSearchRequest;
import com.garosugil.dto.route.RouteSearchResponse;
import com.garosugil.route.AvenueRouteGraphBuilder;
import com.garosugil.route.TimeBucketDpSolver;
import com.garosugil.route.model.Edge;
import com.garosugil.route.model.EdgeType;
import com.garosugil.route.model.RouteGraph;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvenueRouteService {

    private final RoadDataService roadDataService;

    // 후보 박스 확장(약 3km)
    private static final double BOUNDING_BOX_RADIUS_KM = 3.0;

    // 보행 속도: 초당 1.4m(약 5km/h)
    private static final double WALKING_SPEED_MPS = 1.4;

    // 그래프 WALK 연결 제한 (성능 핵심)
    private static final double WALK_LINK_MAX_M = 500.0; // 500m 이내만
    private static final int WALK_LINK_TOP_K = 15;       // 노드당 가까운 K개만 연결

    // 시간 버킷 단위 (성능/정확도 트레이드오프)
    private static final int TIME_BUCKET_SEC = 10;

    /**
     * 여유 경로 탐색 (Graph + Time Bucket DP)
     * 목표: 제한 시간 내 도착 경로 중 "가로수길(AVENUE) 총거리" 최대화
     */
    public RouteSearchResponse.RouteInfo searchAvenueRoute(
            RouteSearchRequest.Location start,
            RouteSearchRequest.Location end,
            int reqAddedTime,
            int fastestTimeMin) {

        log.info("[AVENUE] start={} end={}", start, end);

        // Step 1) 그래프 생성(후보 필터 + 노드/엣지 구성)
        AvenueRouteGraphBuilder builder = new AvenueRouteGraphBuilder(
                roadDataService,
                BOUNDING_BOX_RADIUS_KM,
                WALKING_SPEED_MPS,
                WALK_LINK_MAX_M,
                WALK_LINK_TOP_K
        );

        long t0 = System.nanoTime();

        AvenueRouteGraphBuilder.BuildResult build = builder.build(start, end);
        long t1 = System.nanoTime();

        RouteGraph graph = build.graph;

        int nodeCount = graph.getNodes().size();
        int edgeCount = graph.getAdj().stream().mapToInt(List::size).sum();

        log.info("[AVENUE][GRAPH] candidates={} nodes={} edges={}",
                build.candidateRoadCount, nodeCount, edgeCount);

        if (build.candidateRoadCount == 0) {
            log.warn("[AVENUE] 후보 가로수길이 없습니다. AVENUE 경로를 만들 수 없습니다.");
            return null;
        }

        int maxTimeSec = calculateMaxTimeSec(reqAddedTime, fastestTimeMin);
        TimeBucketDpSolver solver = new TimeBucketDpSolver(maxTimeSec, TIME_BUCKET_SEC);

        TimeBucketDpSolver.DpResult result = solver.solve(graph, build.startNodeId, build.endNodeId);
        long t2 = System.nanoTime();

        log.info("[AVENUE][TIME] buildGraph={}ms, dpSolve={}ms, total={}ms",
                (t1 - t0) / 1_000_000,
                (t2 - t1) / 1_000_000,
                (t2 - t0) / 1_000_000);

        if (!result.isFound()) {
            log.warn("[AVENUE][DP] 제한 시간 내 도착 가능한 경로 없음");
            return null;
        }

        int avenueSegCount = (int) result.getPathEdges().stream().filter(e -> e.getType() == EdgeType.AVENUE).count();

        log.info("[AVENUE][DP] best: avenueDist={}m, time={}s, totalDist={}m, edges={}, avenueSegments={}",
                (int) result.getBestAvenueDistanceM(),
                (int) result.getBestTimeSec(),
                (int) result.getBestTotalDistanceM(),
                result.getPathEdges().size(),
                avenueSegCount);


        // Step 3) RouteSearchResponse.RouteInfo 구성
        return buildAvenueRouteInfo(graph, result.getPathEdges(), start, end,
                result.getBestTotalDistanceM(),
                result.getBestTimeSec(),
                result.getBestAvenueDistanceM(),
                reqAddedTime,
                fastestTimeMin);
    }

    private RouteSearchResponse.RouteInfo buildAvenueRouteInfo(
            RouteGraph graph,
            List<Edge> edges,
            RouteSearchRequest.Location start,
            RouteSearchRequest.Location end,
            double totalDistanceM,
            double totalTimeSec,
            double avenueDistanceM,
            int reqAddedTime,
            int fastestTimeMin
    ) {
        List<RouteSearchResponse.RouteInfo.PathPoint> path = new ArrayList<>();
        addPoint(path, start.getLat(), start.getLng());

        for (Edge e : edges) {
            if (e.getType() == EdgeType.AVENUE) {
                // geometry가 있으면 geometry를 그대로 path에 추가
                if (e.getGeometry() != null && !e.getGeometry().isEmpty()) {
                    for (RouteSearchRequest.Location p : e.getGeometry()) {
                        addPoint(path, p.getLat(), p.getLng());
                    }
                } else {
                    // geometry 없으면 도착 노드 좌표만 추가
                    var toLoc = graph.getNodes().get(e.getTo()).getLoc();
                    addPoint(path, toLoc.getLat(), toLoc.getLng());
                }
            } else {
                // WALK는 직선 이동이므로 "도착 노드 좌표"만 추가(점 밀도는 추후 개선 가능)
                var toLoc = graph.getNodes().get(e.getTo()).getLoc();
                addPoint(path, toLoc.getLat(), toLoc.getLng());
            }
        }

        // 마지막에 목적지 보정(중복이면 addPoint가 자동으로 제거)
        addPoint(path, end.getLat(), end.getLng());

        int durationSec = (int) totalTimeSec;
        int distanceMeter = (int) totalDistanceM;

        RouteSearchResponse.RouteInfo.Summary summary =
                new RouteSearchResponse.RouteInfo.Summary(
                        distanceMeter,
                        durationSec
                );

        // ECO 타입에 tags 추가
        List<String> tags = new ArrayList<>();
        tags.add("▲ 그늘 80%");
        tags.add("● 여유로움");

        return new RouteSearchResponse.RouteInfo("ECO", summary, tags, path);
    }

    private int calculateMaxTimeSec(int reqAddedTime, int fastestTimeMin) {
        int baseTimeMin = fastestTimeMin > 0 ? fastestTimeMin : 15;
        int targetTotalTime = baseTimeMin + Math.max(reqAddedTime, 0);
        return targetTotalTime * 60;
    }

    private String buildDisplayMessage(int targetTotalTime, int actualTimeMin, int reqAddedTime,
                                       int actualAddedTime, double avenueDistanceM) {
        if (reqAddedTime > 0 && actualAddedTime < reqAddedTime) {
            return String.format("최대로 돌아가는 경로입니다. (실제 +%d분 소요)", actualAddedTime);
        }

        String avenueKm = String.format("%.1f", avenueDistanceM / 1000.0);
        return String.format("희망 %d분 (실제 %d분 소요) | 🌲 가로수길 %skm",
                targetTotalTime,
                actualTimeMin,
                avenueKm);
    }

    /**
     * 연속 중복 좌표를 줄여 path 크기를 통제(로그/응답 폭주 방지)
     */
    private void addPoint(List<RouteSearchResponse.RouteInfo.PathPoint> path, double lat, double lng) {
        int n = path.size();
        if (n > 0) {
            RouteSearchResponse.RouteInfo.PathPoint last = path.get(n - 1);
            if (Math.abs(last.getLat() - lat) < 1e-9 && Math.abs(last.getLng() - lng) < 1e-9) {
                return;
            }
        }
        path.add(new RouteSearchResponse.RouteInfo.PathPoint(lat, lng));
    }
}
