package com.convenience.store.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {
    private String name;
    private String phone;
    private String email;
    private String password;
    private int hourlyWage;
    private String role; // MANAGER or STAFF
}