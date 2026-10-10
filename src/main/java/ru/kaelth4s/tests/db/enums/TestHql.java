package ru.kaelth4s.tests.db.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.kaelth4s.framework.db.base.HqlScript;

@Getter
@AllArgsConstructor
public enum TestHql implements HqlScript {
    FIND_BY("hql/findByTemplateParam.hql"),
    FIND_ALL_BY_PROJECT_SORTED_BY("hql/test/findAllByProjectSortedByTemplateParam.hql");

    private final String resourcePath;
}