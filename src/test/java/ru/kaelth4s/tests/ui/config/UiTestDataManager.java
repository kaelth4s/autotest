package ru.kaelth4s.tests.ui.config;

import ru.kaelth4s.framework.util.JsonUtil;
import ru.kaelth4s.tests.ui.dto.UiTestData;

public final class UiTestDataManager {
    private static final String UI_TEST_DATA_RESOURCE_PATH = "ui/test-data.json";
    private static final UiTestData UI_TEST_DATA = load();

    private UiTestDataManager() {
    }

    public static UiTestData getUiTestData() {
        return UI_TEST_DATA;
    }

    private static UiTestData load() {
        return JsonUtil.mapFromResourcesJson(UI_TEST_DATA_RESOURCE_PATH, UiTestData.class);
    }
}