package ru.kaelth4s.api.config;

import ru.kaelth4s.api.dto.ApiTestData;
import ru.kaelth4s.framework.util.JsonUtil;

public final class ApiTestDataManager {
    private static final String API_TEST_DATA_RESOURCE_PATH = "api/test-data.json";
    private static final ApiTestData API_TEST_DATA = load();

    private ApiTestDataManager() {
    }

    public static ApiTestData getApiTestData() {
        return API_TEST_DATA;
    }

    private static ApiTestData load() {
        ApiTestData json = JsonUtil.mapFromResourcesJson(API_TEST_DATA_RESOURCE_PATH, ApiTestData.class);
        return ApiTestData.from(json);
    }
}