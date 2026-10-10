package ru.kaelth4s.tests.db.repository;

import ru.kaelth4s.framework.db.enums.SortType;
import ru.kaelth4s.tests.db.base.Repository;
import ru.kaelth4s.tests.db.model.TestModel;
import ru.kaelth4s.tests.db.model.TestModel_;
import ru.kaelth4s.tests.db.query.hql.test.FindAllByProjectSortedByHqlQuery;

import java.util.List;

public final class TestModelRepository extends Repository<TestModel> {
    public TestModelRepository() {
        super(TestModel.class);
    }

    public TestModel create(TestModel testModel) {
        return save(testModel);
    }

    public List<TestModel> getAllByProjectSortedByStartTime(long projectId, SortType sortType) {
        return hqlQueryList(
                new FindAllByProjectSortedByHqlQuery(
                        TestModel_.START_TIME,
                        sortType,
                        projectId
                ).toQuery(),
                TestModel.class
        );
    }
}