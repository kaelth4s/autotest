package ru.kaelth4s.tests.ui.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record UiTestData(
        String projectName,
        String newProjectName
) {
    @JsonCreator
    public static UiTestData from(
            @JsonProperty("projectName") String projectName,
            @JsonProperty("newProjectName") String newProjectName
    ) {
        return new UiTestData(
                projectName,
                newProjectName
        );
    }
}