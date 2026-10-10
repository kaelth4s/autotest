package ru.kaelth4s.tests.manager;

import ru.kaelth4s.framework.util.JsonUtil;
import ru.kaelth4s.framework.util.RandomFiller;
import ru.kaelth4s.tests.dto.TestData;

public class TestDataManager {
    private static final String testDataFileName = "test-data.json";
    private static TestData testData;

    private TestDataManager() {}

    public static TestData getTestData() {
        if (testData != null) return testData;

        TestData loaded = JsonUtil.mapFromResourcesJson(testDataFileName, TestData.class);
        testData = RandomFiller.fill(loaded);
        return testData;
    }
}