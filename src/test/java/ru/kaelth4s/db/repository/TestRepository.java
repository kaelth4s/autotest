package ru.kaelth4s.db.repository;

import ru.kaelth4s.db.base.Repository;
import ru.kaelth4s.db.enums.HqlScript;
import ru.kaelth4s.db.model.Author;
import ru.kaelth4s.db.model.Project;
import ru.kaelth4s.db.model.SessionModel;
import ru.kaelth4s.db.model.TestModel;
import ru.kaelth4s.db.parameterizedquery.FindRandomTestIds;
import ru.kaelth4s.db.util.HibernateUtil;
import ru.kaelth4s.db.util.SessionModelUtil;

import java.util.ArrayList;
import java.util.List;

public final class TestRepository extends Repository<TestModel> {
    public TestRepository() {
        super(TestModel.class);
    }

    public TestModel addTestResult(TestModel test) {
        return save(test);
    }

    public void updateAll(List<TestModel> tests) {
        inTransaction(session -> {
            tests.forEach(session::merge);
            return null;
        });
    }

    public List<TestModel> findAllByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        try (var session = HibernateUtil.get().openSession()) {
            return session.byMultipleIds(TestModel.class).multiLoad(ids);
        }
    }

    public List<Long> findRandomIdsWithRepeatingDigits(int limit) {
        return nativeQueryList(new FindRandomTestIds(limit), Long.class);
    }

    public List<TestModel> copyTestsWithNewOwner(List<Long> ids, Project project, Author author) {
        return inTransaction(session -> {
            SessionModel sessionModel = SessionModelUtil.get();
            List<TestModel> copies = new ArrayList<>();
            for (Long id : ids) {
                TestModel original = session.get(TestModel.class, id);
                TestModel copy = cloneWithNewOwner(original, project, author, sessionModel);
                session.persist(copy);
                copies.add(copy);
            }
            return copies;
        });
    }

    public void deleteAllByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return;
        }
        inTransaction(session -> {
            List<TestModel> tests = session.byMultipleIds(TestModel.class).multiLoad(ids);
            tests.forEach(session::remove);
            return null;
        });
    }

    public TestModel getLast() {
        return queryOne(HqlScript.FIND_LAST_TEST)
                .orElseThrow(() -> new IllegalStateException("Test table doesnt have any rows"));
    }

    private TestModel cloneWithNewOwner(TestModel src, Project project, Author author, SessionModel sessionModel) {
        TestModel copy = new TestModel();
        copy.setName(src.getName());
        copy.setMethodName(src.getMethodName());
        copy.setEnv(src.getEnv());
        copy.setBrowser(src.getBrowser());
        copy.setStartTime(src.getStartTime());
        copy.setEndTime(src.getEndTime());
        copy.setStatus(src.getStatus());
        copy.setProject(project);
        copy.setAuthor(author);
        copy.setSessionModel(sessionModel);
        return copy;
    }
}