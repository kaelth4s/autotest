package ru.kaelth4s.tests.dto;

public record TestData(
        AlertsTestData alertsTestData,
        IframeTestData iframeTestData,
        TablesTestData tablesTestData,
        SliderTestData sliderTestData,
        DatePickerTestData datePickerTestData
) {}