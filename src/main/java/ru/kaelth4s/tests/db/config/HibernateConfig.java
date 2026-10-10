package ru.kaelth4s.tests.db.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.kaelth4s.tests.db.dto.DbEnvironmentData;
import ru.kaelth4s.tests.db.model.*;

public final class HibernateConfig {

    private static final String DRIVER_CLASS = "hibernate.connection.driver_class";
    private static final String CONNECTION_URL = "hibernate.connection.url";
    private static final String CONNECTION_USERNAME = "hibernate.connection.username";
    private static final String CONNECTION_PASSWORD = "hibernate.connection.password";
    private static final String SHOW_SQL = "hibernate.show_sql";
    private static final String FORMAT_SQL = "hibernate.format_sql";

    private HibernateConfig() {
    }

    public static SessionFactory buildSessionFactory() {
        Configuration cfg = new Configuration();
        applyConnectionSettings(cfg);
        applyMappings(cfg);
        return cfg.buildSessionFactory();
    }

    private static void applyConnectionSettings(Configuration cfg) {
        DbEnvironmentData env = DbEnvironmentDataManager.getData();

        cfg.setProperty(DRIVER_CLASS, env.driver());
        cfg.setProperty(CONNECTION_URL, env.jdbcUrl());
        cfg.setProperty(CONNECTION_USERNAME, env.user());
        cfg.setProperty(CONNECTION_PASSWORD, env.password());
        cfg.setProperty(SHOW_SQL, env.showSql());
        cfg.setProperty(FORMAT_SQL, env.formatSql());
    }

    private static void applyMappings(Configuration cfg) {
        cfg.addAnnotatedClass(TestModel.class);
        cfg.addAnnotatedClass(Project.class);
        cfg.addAnnotatedClass(Author.class);
        cfg.addAnnotatedClass(Status.class);
        cfg.addAnnotatedClass(SessionModel.class);
        cfg.addAnnotatedClass(Log.class);
        cfg.addAnnotatedClass(Attachment.class);
    }
}