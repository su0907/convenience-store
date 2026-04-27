package com.convenience.store.domain.user.dto;

import com.convenience.store.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponse {
    private final Long id;
    private final String name;
    private final String phone;
    private final String email;
    private final String role;
    private final int hourlyWage;

    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.role = user.getRole().name();
        this.hourlyWage = user.getHourlyWage();
    }
}