package ru.kaelth4s.tests.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortAttribute {
    DELETED("deleted"),
    DELETED_DESC("-deleted"),
    CREATED("created"),
    CREATED_DESC("-created");

    private final String value;
}