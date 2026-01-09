package com.garosugil.util;

/**
 * 거리 계산 유틸리티 클래스
 * 하버사인 공식을 사용하여 두 지점 간의 거리를 계산합니다.
 */
public class DistanceUtil {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * 두 좌표 간의 거리를 미터 단위로 계산 (하버사인 공식)
     * @param lat1 첫 번째 지점의 위도
     * @param lng1 첫 번째 지점의 경도
     * @param lat2 두 번째 지점의 위도
     * @param lng2 두 번째 지점의 경도
     * @return 거리 (미터)
     */
    public static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c * 1000; // 미터로 변환
    }
}
