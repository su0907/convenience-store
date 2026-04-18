package com.convenience.store.domain.handover.repository;

import com.convenience.store.domain.handover.entity.Handover;
import com.convenience.store.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HandoverRepository extends JpaRepository<Handover, Long> {
    List<Handover> findByUserOrderByCreatedAtDesc(User user);
    List<Handover> findAllByOrderByCreatedAtDesc();
}