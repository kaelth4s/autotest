package ru.kaelth4s.tests.db.util;

import ru.kaelth4s.tests.db.model.SessionModel;
import ru.kaelth4s.tests.db.repository.SessionModelRepository;

import static aquality.selenium.browser.AqualityServices.getLogger;

public final class SessionModelUtil {

    private static final SessionModel SESSION = create();

    private SessionModelUtil() {
    }

    public static SessionModel get() {
        return SESSION;
    }

    private static SessionModel create() {
        long buildNumber = BuildNumberUtil.resolve();
        SessionModel session = new SessionModelRepository().create(buildNumber);
        getLogger().info("Test session created: id=%s, buildNumber=%s",
                session.getId(), buildNumber);
        return session;
    }
}