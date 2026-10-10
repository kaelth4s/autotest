package ru.kaelth4s.db.repository;

import ru.kaelth4s.db.base.Repository;
import ru.kaelth4s.db.enums.TestStatus;
import ru.kaelth4s.db.model.Status;

public final class StatusRepository extends Repository<Status> {

    private static final String NAME_FIELD = "name";

    public StatusRepository() {
        super(Status.class);
    }

    public Status get(TestStatus status) {
        return findOrCreate(NAME_FIELD, status.getDbName(), () -> new Status(status.getDbName()));
    }
}