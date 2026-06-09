package com.convenience.store.domain.store.controller;

import com.convenience.store.domain.store.dto.StoreLocationRequest;
import com.convenience.store.domain.store.dto.StoreLocationResponse;
import com.convenience.store.domain.store.service.StoreLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreLocationController {

    private final StoreLocationService storeLocationService;

    // 편의점 위치 등록 (점장)
    @PostMapping("/location")
    public ResponseEntity<StoreLocationResponse> registerLocation(
            @RequestBody StoreLocationRequest request) {
        return ResponseEntity.ok(storeLocationService.registerLocation(request));
    }

    // 편의점 위치 조회
    @GetMapping("/location")
    public ResponseEntity<List<StoreLocationResponse>> getAllLocations() {
        return ResponseEntity.ok(storeLocationService.getAllLocations());
    }
}