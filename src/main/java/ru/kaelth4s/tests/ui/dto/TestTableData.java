package ru.kaelth4s.tests.ui.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record TestTableData(
        String testName,
        String testMethod,
        String latestTestResult,
        LocalDateTime latestTestStartTime,
        LocalDateTime latestTestEndTime,
        LocalTime latestTestDuration
) {
}