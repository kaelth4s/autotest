package ru.kaelth4s.tests.dto;

import ru.kaelth4s.framework.util.RandomGenerated;

public record SliderTestData(
        @RandomGenerated("getRandomPercent") int sliderValuePercent,
        int engineerAge,
        int error
){}