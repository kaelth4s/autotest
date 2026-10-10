package ru.kaelth4s.tests.api.config;

import ru.kaelth4s.framework.util.JsonUtil;
import ru.kaelth4s.tests.api.dto.ApiEnvironmentData;

public final class ApiEnvironmentDataManager {
    private static final String API_ENVIRONMENT_DATA_RESOURCE_PATH = "api/environment.json";
    private static final ApiEnvironmentData API_ENVIRONMENT_DATA = load();

    private ApiEnvironmentDataManager() {
    }

    public static ApiEnvironmentData getApiEnvironmentData() {
        return API_ENVIRONMENT_DATA;
    }

    private static ApiEnvironmentData load() {
        return JsonUtil.mapFromResourcesJson(API_ENVIRONMENT_DATA_RESOURCE_PATH, ApiEnvironmentData.class);
    }
}