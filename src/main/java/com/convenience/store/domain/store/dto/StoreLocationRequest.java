package com.convenience.store.domain.store.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreLocationRequest {
    private String name;
    private Double latitude;
    private Double longitude;
}