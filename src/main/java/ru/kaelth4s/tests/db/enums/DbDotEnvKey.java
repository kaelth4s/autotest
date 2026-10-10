package ru.kaelth4s.tests.db.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.kaelth4s.framework.base.DotEnvKey;

@Getter
@AllArgsConstructor
public enum DbDotEnvKey implements DotEnvKey {
    HOST("DB_HOST", "localhost"),
    PORT("DB_PORT", "3306"),
    SCHEMA("DB_SCHEMA", "union_reporting"),
    USER("DB_USER", ""),
    PASSWORD("DB_PASSWORD", ""),
    PARAMS("DB_PARAMS", ""),
    DRIVER("DB_DRIVER", "com.mysql.cj.jdbc.Driver"),
    SHOW_SQL("DB_SHOW_SQL", "true"),
    FORMAT_SQL("DB_FORMAT_SQL", "true");

    private final String key;
    private final String defaultValue;
}