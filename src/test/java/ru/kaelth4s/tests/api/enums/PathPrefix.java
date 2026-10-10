package ru.kaelth4s.tests.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PathPrefix {
    DISK("disk:"),
    TRASH("trash:");

    private final String value;

    public String apply(String path) {
        return value + path;
    }
}