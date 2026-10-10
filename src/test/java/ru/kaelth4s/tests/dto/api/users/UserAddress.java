package ru.kaelth4s.tests.dto.api.users;

public record UserAddress(
        String street,
        String suite,
        String city,
        String zipcode,
        UserAddressGeo geo
) {
}