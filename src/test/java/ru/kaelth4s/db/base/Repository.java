package ru.kaelth4s.db.base;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.kaelth4s.db.enums.HqlScript;
import ru.kaelth4s.db.util.HibernateUtil;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Repository<T> {
    private static final String VALUE_PARAMETER = "value";
    private static final String FIND_BY_FIELD_HQL = """
            FROM %s e
            WHERE e.%s = :%s
            """;

    private final Class<T> entityClass;

    protected Repository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected <R> R inTransaction(Function<Session, R> action) {
        try (Session session = HibernateUtil.get().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                R result = action.apply(session);
                tx.commit();
                return result;
            } catch (RuntimeException runtimeException) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw runtimeException;
            }
        }
    }

    protected T findOrCreate(String field, Object value, Supplier<T> factory) {
        return findBy(field, value).orElseGet(() -> save(factory.get()));
    }

    protected Optional<T> findBy(String field, Object value) {
        String hql = buildFindByFieldHql(field);
        try (Session session = HibernateUtil.get().openSession()) {
            return session.createQuery(hql, entityClass)
                    .setParameter(VALUE_PARAMETER, value)
                    .uniqueResultOptional();
        }
    }

    protected T save(T entity) {
        return inTransaction(session -> {
            session.persist(entity);
            return entity;
        });
    }

    protected Optional<T> queryOne(HqlScript hqlScript) {
        try (Session session = HibernateUtil.get().openSession()) {
            return session.createQuery(hqlScript.hql(), entityClass)
                    .uniqueResultOptional();
        }
    }

    protected <R> List<R> nativeQueryList(ParameterizedSqlQuery sqlQuery, Class<R> type) {
        try (Session session = HibernateUtil.get().openSession()) {
            return session.createNativeQuery(sqlQuery.script().sql(), type)
                    .setProperties(sqlQuery.parameters())
                    .list();
        }
    }

    private String buildFindByFieldHql(String field) {
        return FIND_BY_FIELD_HQL.formatted(entityClass.getSimpleName(), field, VALUE_PARAMETER);
    }
}