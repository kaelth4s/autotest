package ru.kaelth4s.tests.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoint {
    TOKEN_GET("/token/get");

    private final String path;
}