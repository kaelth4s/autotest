package ru.kaelth4s.tests.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Query {
    PERMANENTLY("permanently"),
    SORT("sort"),
    FROM("from"),
    PATH("path"),
    OVERWRITE("overwrite");

    private final String value;
}