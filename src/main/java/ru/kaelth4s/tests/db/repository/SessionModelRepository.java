package ru.kaelth4s.tests.db.repository;

import ru.kaelth4s.tests.db.base.Repository;
import ru.kaelth4s.tests.db.model.SessionModel;

import java.sql.Timestamp;

public final class SessionModelRepository extends Repository<SessionModel> {
    public SessionModelRepository() {
        super(SessionModel.class);
    }

    public SessionModel create(Long buildNumber) {
        return save(new SessionModel(
                "session-%s".formatted(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                buildNumber
        ));
    }
}