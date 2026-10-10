package ru.kaelth4s.db.base;

import ru.kaelth4s.db.enums.SqlScript;

import java.lang.reflect.RecordComponent;
import java.util.HashMap;
import java.util.Map;

public interface ParameterizedSqlQuery {
    SqlScript script();

    default Map<String, Object> parameters() {
        RecordComponent[] components = getClass().getRecordComponents();
        if (components == null) {
            throw new IllegalStateException(String.format("%s must be a record to use SqlQuery", getClass().getSimpleName()));
        }
        Map<String, Object> params = new HashMap<>();
        for (RecordComponent c : components) {
            try {
                params.put(c.getName(), c.getAccessor().invoke(this));
            } catch (ReflectiveOperationException exception) {
                throw new IllegalStateException(String.format("Failed to read parameter %s", c.getName()), exception);
            }
        }
        return params;
    }
}