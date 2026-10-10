package ru.kaelth4s.tests.db.base;

import jakarta.persistence.metamodel.SingularAttribute;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.kaelth4s.framework.db.enums.SortType;
import ru.kaelth4s.framework.db.query.HqlQuery;
import ru.kaelth4s.tests.db.query.hql.FindByHqlQuery;
import ru.kaelth4s.tests.db.util.HibernateUtil;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Repository<T> {
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

    protected T findByOrCreate(String field, Object value, Supplier<T> factory) {
        return findBy(field, value).orElseGet(() -> save(factory.get()));
    }

    protected Optional<T> findBy(String field, Object value) {
        return hqlQueryOne(new FindByHqlQuery(entityClass.getSimpleName(), field, value).toQuery(), entityClass);
    }

    protected T save(T entity) {
        return inTransaction(session -> {
            session.persist(entity);
            return entity;
        });
    }

    protected <R> Optional<R> hqlQueryOne(HqlQuery query, Class<R> type) {
        try (Session session = HibernateUtil.get().openSession()) {
            var q = session.createQuery(query.substitute(), type);
            if (!query.namedParams().isEmpty()) {
                q.setProperties(query.namedParams());
            }
            return q.uniqueResultOptional();
        }
    }

    protected <R> List<R> hqlQueryList(HqlQuery query, Class<R> type) {
        try (Session session = HibernateUtil.get().openSession()) {
            var q = session.createQuery(query.substitute(), type);
            if (!query.namedParams().isEmpty()) {
                q.setProperties(query.namedParams());
            }
            return q.list();
        }
    }
}