package ru.kaelth4s.tests.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoint {
    TRASH_RESOURCES_RESTORE("/trash/resources/restore"),
    TRASH_RESOURCES("/trash/resources"),
    RESOURCES("/resources"),
    RESOURCES_UPLOAD("/resources/upload"),
    RESOURCES_MOVE("/resources/move");

    private final String path;
}