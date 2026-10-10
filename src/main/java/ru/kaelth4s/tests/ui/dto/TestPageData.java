package ru.kaelth4s.tests.ui.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record TestPageData(
        String projectName,
        String testName,
        String testMethodName,
        String status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        LocalTime duration,
        String environment,
        String browser
) {
}