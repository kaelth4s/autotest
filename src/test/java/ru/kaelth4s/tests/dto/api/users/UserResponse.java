package ru.kaelth4s.tests.dto.api.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserResponse(
        int id,
        String name,
        String username,
        String email,
        UserAddress address,
        String phone,
        String website,
        UserCompany company
) {
}