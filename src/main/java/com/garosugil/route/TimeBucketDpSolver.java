package com.garosugil.route;

import com.garosugil.route.model.Edge;
import com.garosugil.route.model.RouteGraph;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimeBucketDpSolver {

    @Getter
    public static class DpResult {
        private final boolean found;
        private final double bestAvenueDistanceM;
        private final double bestTimeSec;
        private final double bestTotalDistanceM;
        private final List<Edge> pathEdges;

        public DpResult(boolean found,
                        double bestAvenueDistanceM,
                        double bestTimeSec,
                        double bestTotalDistanceM,
                        List<Edge> pathEdges) {
            this.found = found;
            this.bestAvenueDistanceM = bestAvenueDistanceM;
            this.bestTimeSec = bestTimeSec;
            this.bestTotalDistanceM = bestTotalDistanceM;
            this.pathEdges = pathEdges;
        }
    }

    private final int maxTimeSec;   // 예: 3600
    private final int bucketSec;    // 예: 10

    public TimeBucketDpSolver(int maxTimeSec, int bucketSec) {
        this.maxTimeSec = maxTimeSec;
        this.bucketSec = bucketSec;
    }

    public DpResult solve(RouteGraph g, int startId, int endId) {
        int V = g.getNodes().size();
        int B = (int) Math.ceil((double) maxTimeSec / bucketSec);

        final double NEG = -1e18;

        double[][] dpAvenue = new double[B + 1][V];
        double[][] dpTime = new double[B + 1][V];
        double[][] dpTotalDist = new double[B + 1][V];

        int[][] prevNode = new int[B + 1][V];
        int[][] prevBucket = new int[B + 1][V];
        Edge[][] prevEdge = new Edge[B + 1][V];

        for (int b = 0; b <= B; b++) {
            for (int v = 0; v < V; v++) {
                dpAvenue[b][v] = NEG;
                dpTime[b][v] = Double.POSITIVE_INFINITY;
                dpTotalDist[b][v] = Double.POSITIVE_INFINITY;
                prevNode[b][v] = -1;
                prevBucket[b][v] = -1;
                prevEdge[b][v] = null;
            }
        }

        dpAvenue[0][startId] = 0.0;
        dpTime[0][startId] = 0.0;
        dpTotalDist[0][startId] = 0.0;

        for (int b = 0; b <= B; b++) {
            for (int v = 0; v < V; v++) {
                if (dpAvenue[b][v] <= NEG / 2) continue;

                for (Edge e : g.getAdj().get(v)) {
                    double newTime = dpTime[b][v] + e.getCostSec();
                    if (newTime > maxTimeSec) continue;

                    int nb = (int) Math.ceil(newTime / bucketSec);
                    if (nb > B) continue;

                    double newAvenue = dpAvenue[b][v] + e.getRewardAvenueM();
                    double newTotalDist = dpTotalDist[b][v] + e.getTotalDistanceM();

                    if (isBetter(newAvenue, newTime, newTotalDist,
                            dpAvenue[nb][e.getTo()], dpTime[nb][e.getTo()], dpTotalDist[nb][e.getTo()])) {

                        dpAvenue[nb][e.getTo()] = newAvenue;
                        dpTime[nb][e.getTo()] = newTime;
                        dpTotalDist[nb][e.getTo()] = newTotalDist;

                        prevNode[nb][e.getTo()] = v;
                        prevBucket[nb][e.getTo()] = b;
                        prevEdge[nb][e.getTo()] = e;
                    }
                }
            }
        }

        // endId 도착 상태 중 최적 선택
        int bestB = -1;
        double bestA = NEG, bestT = Double.POSITIVE_INFINITY, bestD = Double.POSITIVE_INFINITY;

        for (int b = 0; b <= B; b++) {
            if (dpAvenue[b][endId] <= NEG / 2) continue;
            if (isBetter(dpAvenue[b][endId], dpTime[b][endId], dpTotalDist[b][endId], bestA, bestT, bestD)) {
                bestA = dpAvenue[b][endId];
                bestT = dpTime[b][endId];
                bestD = dpTotalDist[b][endId];
                bestB = b;
            }
        }

        if (bestB < 0) {
            return new DpResult(false, 0, 0, 0, List.of());
        }

        // 경로 복원
        List<Edge> edges = new ArrayList<>();
        int curNode = endId;
        int curB = bestB;

        while (!(curB == 0 && curNode == startId)) {
            Edge pe = prevEdge[curB][curNode];
            int pn = prevNode[curB][curNode];
            int pb = prevBucket[curB][curNode];

            if (pe == null || pn < 0 || pb < 0) break;

            edges.add(pe);
            curNode = pn;
            curB = pb;
        }

        Collections.reverse(edges);
        return new DpResult(true, bestA, bestT, bestD, edges);
    }

    /**
     * 우선순위:
     * 1) avenueDistance(가로수길 총거리) 최대
     * 2) time(총 시간) 최소
     * 3) totalDistance(총 이동거리) 최소
     */
    private boolean isBetter(double a1, double t1, double d1, double a2, double t2, double d2) {
        if (a1 > a2 + 1e-9) return true;
        if (a1 < a2 - 1e-9) return false;

        if (t1 < t2 - 1e-9) return true;
        if (t1 > t2 + 1e-9) return false;

        return d1 < d2 - 1e-9;
    }
}
