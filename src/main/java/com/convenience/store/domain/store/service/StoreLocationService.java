package com.convenience.store.domain.store.service;

import com.convenience.store.domain.store.dto.StoreLocationRequest;
import com.convenience.store.domain.store.dto.StoreLocationResponse;
import com.convenience.store.domain.store.entity.StoreLocation;
import com.convenience.store.domain.store.repository.StoreLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreLocationService {

    private final StoreLocationRepository storeLocationRepository;

    // 편의점 위치 등록 (점장)
    @Transactional
    public StoreLocationResponse registerLocation(StoreLocationRequest request) {
        StoreLocation location = StoreLocation.builder()
                .name(request.getName())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
        return new StoreLocationResponse(storeLocationRepository.save(location));
    }

    // 편의점 위치 조회
    public List<StoreLocationResponse> getAllLocations() {
        return storeLocationRepository.findAll()
                .stream()
                .map(StoreLocationResponse::new)
                .toList();
    }

    // 반경 500m 이내 확인
    public boolean isWithinRange(Double userLat, Double userLng) {
        return storeLocationRepository.findAll()
                .stream()
                .anyMatch(location -> {
                    double distance = calculateDistance(
                            userLat, userLng,
                            location.getLatitude(), location.getLongitude()
                    );
                    return distance <= 500;
                });
    }

    // Haversine 공식으로 거리 계산 (미터)
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371000; // 지구 반지름 (미터)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}