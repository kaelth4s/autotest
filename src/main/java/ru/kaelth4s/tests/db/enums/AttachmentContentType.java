package ru.kaelth4s.tests.db.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AttachmentContentType {
    PNG("image/png");

    private final String name;
}