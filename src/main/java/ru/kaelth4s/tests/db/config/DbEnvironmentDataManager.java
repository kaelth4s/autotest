package ru.kaelth4s.tests.db.config;

import ru.kaelth4s.tests.db.dto.DbEnvironmentData;

import static aquality.selenium.browser.AqualityServices.getLogger;
import static ru.kaelth4s.framework.config.DotEnvManager.resolveDotEnv;
import static ru.kaelth4s.tests.db.enums.DbDotEnvKey.*;

public final class DbEnvironmentDataManager {
    private static final DbEnvironmentData DATA = load();

    private DbEnvironmentDataManager() {
    }

    public static DbEnvironmentData getData() {
        return DATA;
    }

    private static DbEnvironmentData load() {
        DbEnvironmentData data = new DbEnvironmentData(
                resolveDotEnv(HOST),
                resolveDotEnv(PORT),
                resolveDotEnv(SCHEMA),
                resolveDotEnv(USER),
                resolveDotEnv(PASSWORD),
                resolveDotEnv(PARAMS),
                resolveDotEnv(DRIVER),
                resolveDotEnv(SHOW_SQL),
                resolveDotEnv(FORMAT_SQL)
        );

        getLogger().info("DB connection: %s:%s/%s as %s",
                data.host(), data.port(), data.schema(), data.user());

        return data;
    }
}