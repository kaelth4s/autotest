package ru.kaelth4s.db.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DbEnvironmentKey {
    HOST("DB_HOST", "localhost"),
    PORT("DB_PORT", "3306"),
    SCHEMA("DB_SCHEMA", "union_reporting"),
    USER("DB_USER", "root"),
    PASSWORD("DB_PASSWORD", ""),
    PARAMS("DB_PARAMS", "useSSL=false&serverTimezone=Europe/Moscow&characterEncoding=utf8");

    private final String key;
    private final String defaultValue;
}