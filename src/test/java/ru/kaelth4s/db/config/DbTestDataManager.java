package ru.kaelth4s.db.config;

import ru.kaelth4s.db.dto.DbTestData;
import ru.kaelth4s.framework.util.JsonUtil;

public final class DbTestDataManager {
    private static final String DB_TEST_DATA_RESOURCE_PATH = "db/test-data.json";
    private static final DbTestData DB_TEST_DATA = load();

    private DbTestDataManager() {
    }

    public static DbTestData getDbTestData() {
        return DB_TEST_DATA;
    }

    private static DbTestData load() {
        DbTestData json = JsonUtil.mapFromResourcesJson(DB_TEST_DATA_RESOURCE_PATH, DbTestData.class);
        return DbTestData.from(json);
    }
}