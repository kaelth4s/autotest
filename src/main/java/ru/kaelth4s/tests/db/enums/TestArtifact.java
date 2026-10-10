package ru.kaelth4s.tests.db.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TestArtifact {
    LOG_FILE("logs/test.log");

    private final String path;
}