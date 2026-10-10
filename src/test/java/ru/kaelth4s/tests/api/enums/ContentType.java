package ru.kaelth4s.tests.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContentType {
    JSON("application/json"),
    OCTET("application/octet-stream");

    private final String value;
}