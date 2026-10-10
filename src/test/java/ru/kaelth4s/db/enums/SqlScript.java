package ru.kaelth4s.db.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static ru.kaelth4s.framework.util.FileUtil.readResource;

@Getter
@AllArgsConstructor
public enum SqlScript {
    FIND_RANDOM_TEST_IDS_WITH_REPEATING_DIGITS("sql/test/findRandomIdsWithRepeatingDigits.sql");

    private final String resourcePath;

    public String sql() {
        return readResource(resourcePath);
    }
}