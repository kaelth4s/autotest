package ru.kaelth4s.tests.db.repository;

import ru.kaelth4s.tests.db.base.Repository;
import ru.kaelth4s.tests.db.model.Log;

public final class LogRepository extends Repository<Log> {
    public LogRepository() {
        super(Log.class);
    }

    public Log create(Log log) {
        return save(log);
    }
}