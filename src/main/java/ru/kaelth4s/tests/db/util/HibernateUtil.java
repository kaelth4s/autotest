package ru.kaelth4s.tests.db.util;

import org.hibernate.SessionFactory;
import ru.kaelth4s.tests.db.config.HibernateConfig;

public final class HibernateUtil {

    private static final SessionFactory FACTORY = HibernateConfig.buildSessionFactory();

    private HibernateUtil() {
    }

    public static SessionFactory get() {
        return FACTORY;
    }
}