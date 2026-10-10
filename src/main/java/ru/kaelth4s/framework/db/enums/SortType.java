package ru.kaelth4s.framework.db.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortType {
    DESC("DESC"),
    ASC("ASC");

    private final String value;
}