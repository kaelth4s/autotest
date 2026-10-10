package ru.kaelth4s.db.repository;

import ru.kaelth4s.db.base.Repository;
import ru.kaelth4s.db.model.SessionModel;

import java.sql.Timestamp;

public final class SessionModelRepository extends Repository<SessionModel> {

    private static final String SESSION_KEY_FIELD = "sessionKey";

    public SessionModelRepository() {
        super(SessionModel.class);
    }

    public SessionModel getOrCreate(SessionModel sessionModel) {
        return findOrCreate(SESSION_KEY_FIELD, sessionModel.getSessionKey(), () -> sessionModel);
    }

    public SessionModel createNew(Long buildNumber) {
        return save(new SessionModel(
                String.format("session-%s", System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()),
                buildNumber
        ));
    }
}