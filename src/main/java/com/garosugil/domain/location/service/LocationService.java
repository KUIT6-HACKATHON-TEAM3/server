package com.garosugil.domain.location.service;

import com.garosugil.domain.location.dto.LocationRequest;
import com.garosugil.domain.location.dto.LocationResponse;
import com.garosugil.domain.location.entity.Location;
import com.garosugil.domain.location.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class LocationService {

    private final LocationRepository locationRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    /**
     * 새로운 위치 생성
     */
    @Transactional
    public LocationResponse createLocation(LocationRequest request) {
        Point point = geometryFactory.createPoint(
                new Coordinate(request.getLongitude(), request.getLatitude())
        );

        Location location = Location.builder()
                .name(request.getName())
                .description(request.getDescription())
                .coordinates(point)
                .address(request.getAddress())
                .build();

        Location savedLocation = locationRepository.save(location);
        log.info("Created location: {}", savedLocation.getId());

        return LocationResponse.from(savedLocation);
    }

    /**
     * 모든 위치 조회
     */
    public List<LocationResponse> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(LocationResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * ID로 위치 조회
     */
    public LocationResponse getLocationById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Location not found: " + id));
        return LocationResponse.from(location);
    }

    /**
     * 반경 내 위치 검색
     */
    public List<LocationResponse> getLocationsWithinRadius(
            double longitude, double latitude, double radiusInMeters
    ) {
        List<Location> locations = locationRepository.findLocationsWithinRadius(
                longitude, latitude, radiusInMeters
        );
        return locations.stream()
                .map(LocationResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 이름으로 위치 검색
     */
    public List<LocationResponse> searchLocationsByName(String name) {
        return locationRepository.findByNameContaining(name).stream()
                .map(LocationResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 위치 삭제
     */
    @Transactional
    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new IllegalArgumentException("Location not found: " + id);
        }
        locationRepository.deleteById(id);
        log.info("Deleted location: {}", id);
    }

}
