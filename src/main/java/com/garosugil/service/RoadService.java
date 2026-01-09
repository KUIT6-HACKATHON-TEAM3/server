package com.garosugil.service;

import com.garosugil.domain.road.StreetSegment;
import com.garosugil.dto.road.NearbyRoadResponse;
import com.garosugil.dto.road.NearbyRoadResponse.Location;
import com.garosugil.dto.road.RoadDetailResponse;
import com.garosugil.dto.road.RoadTagStatsResponse;
import com.garosugil.repository.StreetSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoadService {

    private final RoadDataService roadDataService;
    private final StreetSegmentRepository streetSegmentRepository;
    private final RoadTagService roadTagService;

    public RoadDetailResponse getRoadDetail(Integer segmentId, Long currentUserId) {
        long totalLikeCount = 0;
        boolean isLiked = false;
        String roadName = roadDataService.getRoadName(segmentId);
        
        // 태그 통계 조회 및 상위 태그 추출
        RoadTagStatsResponse tagStats = roadTagService.getStats(segmentId.longValue(), currentUserId);
        List<String> topTags = tagStats.getStats().stream()
                .limit(3) // 상위 3개만
                .map(item -> item.getEmoji() + " " + item.getLabel())
                .collect(Collectors.toList());
        
        return new RoadDetailResponse(segmentId.longValue(), roadName, totalLikeCount, isLiked, topTags);
    }

    public List<NearbyRoadResponse> getNearbyRoads(double minLat, double minLng, double maxLat, double maxLng) {
        List<StreetSegment> segments = streetSegmentRepository.findSegmentsInBoundingBox(minLat, minLng, maxLat, maxLng);
        List<NearbyRoadResponse> results = new ArrayList<>();
        for (StreetSegment segment : segments) {
            List<Location> polyline = lineStringToLocations(segment.getCoordinates());
            NearbyRoadResponse dto = new NearbyRoadResponse(
                segment.getId(),
                segment.getRoadName(),
                segment.getHasTrees(),
                polyline
            );
            results.add(dto);
        }
        return results;
    }

    private List<Location> lineStringToLocations(LineString line) {
        List<Location> list = new ArrayList<>();
        if (line == null) return list;
        for (Coordinate c : line.getCoordinates()) {
            list.add(new Location(c.getY(), c.getX())); // lat(Y), lng(X)
        }
        return list;
    }
}
