package ru.kaelth4s.tests.db.repository;

import jakarta.persistence.metamodel.SingularAttribute;
import ru.kaelth4s.tests.db.base.Repository;
import ru.kaelth4s.tests.db.enums.TestStatus;
import ru.kaelth4s.tests.db.model.Status;
import ru.kaelth4s.tests.db.model.Status_;

public final class StatusRepository extends Repository<Status> {
    public StatusRepository() {
        super(Status.class);
    }

    public Status getByNameOrCreate(TestStatus testStatus) {
        return findByOrCreate(Status_.NAME, testStatus.getDbName(), () -> new Status(testStatus.getDbName()));
    }
}