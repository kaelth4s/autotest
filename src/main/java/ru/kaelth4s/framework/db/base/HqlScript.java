package ru.kaelth4s.framework.db.base;

import ru.kaelth4s.framework.util.FileUtil;

public interface HqlScript {
    String getResourcePath();

    default String hql() {
        return FileUtil.readResource(getResourcePath());
    }
}