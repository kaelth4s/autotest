package ru.kaelth4s.tests.ui.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum TestPageField {
    PROJECT_NAME("Project name"),
    TEST_NAME("Test name"),
    TEST_METHOD_NAME("Test method name"),
    STATUS("Status"),
    ENVIRONMENT("Environment"),
    BROWSER("Browser");

    private final String title;

    public static TestPageField findByTitleOrNull(String title) {
        return Arrays.stream(values())
                .filter(field -> field.title.equals(title))
                .findFirst()
                .orElse(null);
    }
}