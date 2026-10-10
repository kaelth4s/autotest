package ru.kaelth4s.db.mock;

import ru.kaelth4s.db.enums.TestStatus;
import ru.kaelth4s.db.model.TestModel;
import ru.kaelth4s.db.repository.StatusRepository;
import ru.kaelth4s.framework.util.RandomUtil;

import java.sql.Timestamp;

public final class TestRunSimulator {
    private static final long MIN_TEST_TIME_DURATION_SEC = 1;
    private static final long MAX_TEST_TIME_DURATION_SEC = 20;
    private static final StatusRepository statusRepository = new StatusRepository();

    public static void simulate(TestModel test) {
        test.setStartTime(new Timestamp(System.currentTimeMillis()));
        test.setEndTime(new Timestamp(test.getStartTime().getTime() + RandomUtil.randomLong(MIN_TEST_TIME_DURATION_SEC, MAX_TEST_TIME_DURATION_SEC) * 1000));
        test.setStatus(statusRepository.get(TestStatus.PASSED));
    }
}