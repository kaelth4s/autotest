package ru.kaelth4s.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoint {
    POSTS("/posts"),
    USERS("/users");

    private final String path;

    public String byId() {
        return path + "/{id}";
    }
}