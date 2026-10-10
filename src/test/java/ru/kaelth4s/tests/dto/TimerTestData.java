package ru.kaelth4s.tests.dto;

import ru.kaelth4s.tests.dto.json.TimerTestDataJson;

import java.time.LocalTime;

public record TimerTestData(
        LocalTime expectedTimerStartTime
) {
    public static TimerTestData from(TimerTestDataJson json) {
        return new TimerTestData(
                LocalTime.parse(json.expectedTimerStart())
        );
    }
}