package ru.kaelth4s.db.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static ru.kaelth4s.framework.util.FileUtil.readResource;

@Getter
@AllArgsConstructor
public enum HqlScript {
    FIND_LAST_TEST("hql/test/findLast.hql");

    private final String resourcePath;

    public String hql() {
        return readResource(resourcePath);
    }
}