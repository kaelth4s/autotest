package ru.kaelth4s.tests.config;

import ru.kaelth4s.framework.util.JsonUtil;
import ru.kaelth4s.tests.dto.TestData;

public final class TestDataManager {
    private static final String TEST_DATA_RESOURCE_PATH = "test-data.json";
    private static final TestData TEST_DATA = load();

    private TestDataManager() {
    }

    public static TestData getTestData() {
        return TEST_DATA;
    }

    private static TestData load() {
        TestData json = JsonUtil.mapFromResourcesJson(TEST_DATA_RESOURCE_PATH, TestData.class);
        return TestData.from(json);
    }
}