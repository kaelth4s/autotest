package ru.kaelth4s.tests.ui.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Attribute {
    SRC("src");

    private final String name;
}