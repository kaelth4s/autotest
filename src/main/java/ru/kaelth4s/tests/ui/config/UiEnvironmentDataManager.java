package ru.kaelth4s.tests.ui.config;

import ru.kaelth4s.framework.util.JsonUtil;
import ru.kaelth4s.tests.ui.dto.UiEnvironmentData;

public final class UiEnvironmentDataManager {
    private static final String UI_ENVIRONMENT_DATA_RESOURCE_PATH = "ui/environment.json";
    private static final UiEnvironmentData UI_ENVIRONMENT_DATA = load();

    private UiEnvironmentDataManager() {
    }

    public static UiEnvironmentData getUiEnvironmentData() {
        return UI_ENVIRONMENT_DATA;
    }

    private static UiEnvironmentData load() {
        return JsonUtil.mapFromResourcesJson(UI_ENVIRONMENT_DATA_RESOURCE_PATH, UiEnvironmentData.class);
    }
}