package com.garosugil.domain.location.controller;

import com.garosugil.domain.location.dto.LocationRequest;
import com.garosugil.domain.location.dto.LocationResponse;
import com.garosugil.domain.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    /**
     * 새로운 위치 생성
     */
    @PostMapping
    public ResponseEntity<LocationResponse> createLocation(@RequestBody LocationRequest request) {
        LocationResponse response = locationService.createLocation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 모든 위치 조회
     */
    @GetMapping
    public ResponseEntity<List<LocationResponse>> getAllLocations() {
        List<LocationResponse> locations = locationService.getAllLocations();
        return ResponseEntity.ok(locations);
    }

    /**
     * ID로 위치 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<LocationResponse> getLocationById(@PathVariable Long id) {
        LocationResponse response = locationService.getLocationById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 반경 내 위치 검색
     * @param longitude 경도
     * @param latitude 위도
     * @param radius 반경 (미터, 기본값: 1000m)
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<LocationResponse>> getLocationsWithinRadius(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam(defaultValue = "1000") double radius
    ) {
        List<LocationResponse> locations = locationService.getLocationsWithinRadius(
                longitude, latitude, radius
        );
        return ResponseEntity.ok(locations);
    }

    /**
     * 이름으로 위치 검색
     */
    @GetMapping("/search")
    public ResponseEntity<List<LocationResponse>> searchLocationsByName(
            @RequestParam String name
    ) {
        List<LocationResponse> locations = locationService.searchLocationsByName(name);
        return ResponseEntity.ok(locations);
    }

    /**
     * 위치 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }

}
