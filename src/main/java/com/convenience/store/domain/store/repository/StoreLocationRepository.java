package com.convenience.store.domain.store.repository;

import com.convenience.store.domain.store.entity.StoreLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreLocationRepository extends JpaRepository<StoreLocation, Long> {
}