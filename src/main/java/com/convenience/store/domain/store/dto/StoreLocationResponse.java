package com.convenience.store.domain.store.dto;

import com.convenience.store.domain.store.entity.StoreLocation;
import lombok.Getter;

@Getter
public class StoreLocationResponse {
    private final Long id;
    private final String name;
    private final Double latitude;
    private final Double longitude;

    public StoreLocationResponse(StoreLocation storeLocation) {
        this.id = storeLocation.getId();
        this.name = storeLocation.getName();
        this.latitude = storeLocation.getLatitude();
        this.longitude = storeLocation.getLongitude();
    }
}