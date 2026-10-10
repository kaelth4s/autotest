package ru.kaelth4s.tests.ui.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TestTableColumn {
    TEST_NAME("Test name"),
    TEST_METHOD("Test method"),
    TEST_RESULT("Latest test result"),
    START_TIME("Latest test start time"),
    END_TIME("Latest test end time"),
    DURATION("Latest test duration (H:m:s.S)"),
    HISTORY("History");

    private final String title;
}