package ru.kaelth4s.tests.db.config;

import ru.kaelth4s.framework.util.JsonUtil;
import ru.kaelth4s.tests.db.dto.DbTestData;

public final class DbTestDataManager {
    private static final String DB_TEST_DATA_RESOURCE_PATH = "db/test-data.json";
    private static final DbTestData DB_TEST_DATA = load();

    private DbTestDataManager() {
    }

    public static DbTestData getDbTestData() {
        return DB_TEST_DATA;
    }

    private static DbTestData load() {
        return JsonUtil.mapFromResourcesJson(DB_TEST_DATA_RESOURCE_PATH, DbTestData.class);
    }
}