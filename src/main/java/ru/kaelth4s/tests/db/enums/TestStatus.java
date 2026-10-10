package ru.kaelth4s.tests.db.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TestStatus {
    PASSED("PASSED"),
    FAILED("FAILED"),
    SKIPPED("SKIPPED");

    private final String dbName;
}