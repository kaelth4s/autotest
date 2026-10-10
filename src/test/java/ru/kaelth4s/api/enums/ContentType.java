package ru.kaelth4s.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContentType {
    JSON("application/json");

    private final String value;

}