package ru.kaelth4s.tests.config;

import ru.kaelth4s.framework.util.JsonUtil;
import ru.kaelth4s.tests.dto.EnvironmentData;

public final class EnvironmentDataManager {
    private static final String ENVIRONMENT_DATA_RESOURCE_PATH = "environment.json";
    private static final EnvironmentData ENVIRONMENT_DATA = load();

    private EnvironmentDataManager() {
    }

    public static EnvironmentData getEnvironmentData() {
        return ENVIRONMENT_DATA;
    }

    private static EnvironmentData load() {
        return JsonUtil.mapFromResourcesJson(ENVIRONMENT_DATA_RESOURCE_PATH, EnvironmentData.class);
    }
}