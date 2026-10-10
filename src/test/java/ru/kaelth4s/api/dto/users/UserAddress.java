package ru.kaelth4s.api.dto.users;

public record UserAddress(
        String street,
        String suite,
        String city,
        String zipcode,
        UserAddressGeo geo
) {
}