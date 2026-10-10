package ru.kaelth4s.tests.db.query.hql;

import ru.kaelth4s.framework.db.query.HqlQuery;
import ru.kaelth4s.tests.db.enums.TestHql;

import java.util.Map;

public record FindByHqlQuery(
        String entityName,
        String field,
        Object value
) {
    private static final String ENTITY_NAME_PLACEHOLDER = "entity";
    private static final String FIELD_PLACEHOLDER = "field";
    private static final String VALUE_PARAM = "value";

    public HqlQuery toQuery() {
        return HqlQuery.builder(TestHql.FIND_BY)
                .substitutions(Map.of(
                        ENTITY_NAME_PLACEHOLDER, entityName,
                        FIELD_PLACEHOLDER, field
                ))
                .namedParam(VALUE_PARAM, value)
                .build();
    }
}