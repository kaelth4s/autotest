package ru.kaelth4s.db.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.kaelth4s.db.config.DbEnvironmentDataManager;
import ru.kaelth4s.db.dto.DbEnvironmentData;

public final class HibernateUtil {
    private static final String CONNECTION_URL = "hibernate.connection.url";
    private static final String CONNECTION_USERNAME = "hibernate.connection.username";
    private static final String CONNECTION_PASSWORD = "hibernate.connection.password";

    private static final SessionFactory FACTORY = build();

    private HibernateUtil() {
    }

    private static SessionFactory build() {
        DbEnvironmentData env = DbEnvironmentDataManager.getData();

        Configuration cfg = new Configuration().configure();
        cfg.setProperty(CONNECTION_URL, env.jdbcUrl());
        cfg.setProperty(CONNECTION_USERNAME, env.user());
        cfg.setProperty(CONNECTION_PASSWORD, env.password());
        return cfg.buildSessionFactory();
    }

    public static SessionFactory get() {
        return FACTORY;
    }

    public static void shutdown() {
        FACTORY.close();
    }
}