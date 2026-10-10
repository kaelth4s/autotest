package ru.kaelth4s.db.parameterizedquery;

import ru.kaelth4s.db.base.ParameterizedSqlQuery;
import ru.kaelth4s.db.enums.SqlScript;

public record FindRandomTestIds(int limit) implements ParameterizedSqlQuery {
    @Override
    public SqlScript script() {
        return SqlScript.FIND_RANDOM_TEST_IDS_WITH_REPEATING_DIGITS;
    }
}