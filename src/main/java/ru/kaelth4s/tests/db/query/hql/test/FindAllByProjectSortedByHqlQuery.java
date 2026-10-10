package ru.kaelth4s.tests.db.query.hql.test;

import ru.kaelth4s.framework.db.enums.SortType;
import ru.kaelth4s.framework.db.query.HqlQuery;
import ru.kaelth4s.tests.db.enums.TestHql;

import java.util.Map;

public record FindAllByProjectSortedByHqlQuery(
        String field,
        SortType sortType,
        long projectId
) {
    private static final String FIELD_PLACEHOLDER = "field";
    private static final String SORT_TYPE_PLACEHOLDER = "sort_type";
    private static final String PROJECT_ID_PARAM = "project_id";

    public HqlQuery toQuery() {
        return HqlQuery.builder(TestHql.FIND_ALL_BY_PROJECT_SORTED_BY)
                .substitutions(Map.of(
                        FIELD_PLACEHOLDER, field,
                        SORT_TYPE_PLACEHOLDER, sortType.getValue()
                ))
                .namedParam(PROJECT_ID_PARAM, projectId)
                .build();
    }
}