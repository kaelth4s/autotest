package ru.kaelth4s.db.config;

import io.github.cdimascio.dotenv.Dotenv;
import ru.kaelth4s.db.dto.DbEnvironmentData;
import ru.kaelth4s.db.enums.DbEnvironmentKey;

import static aquality.selenium.browser.AqualityServices.getLogger;
import static ru.kaelth4s.db.enums.DbEnvironmentKey.*;

public final class DbEnvironmentDataManager {
    private static final Dotenv DOTENV = Dotenv.configure()
            .ignoreIfMissing()
            .load();
    private static final DbEnvironmentData DATA = load();

    private DbEnvironmentDataManager() {
    }

    public static DbEnvironmentData getData() {
        return DATA;
    }

    private static DbEnvironmentData load() {
        DbEnvironmentData data = new DbEnvironmentData(
                resolve(HOST),
                resolve(PORT),
                resolve(SCHEMA),
                resolve(USER),
                resolve(PASSWORD),
                resolve(PARAMS)
        );

        getLogger().info("DB connection: %s:%s/%s as %s",
                data.host(), data.port(), data.schema(), data.user());

        return data;
    }

    private static String resolve(DbEnvironmentKey envKey) {
        String fromDotenv = DOTENV.get(envKey.getKey());
        if (fromDotenv != null && !fromDotenv.isBlank()) {
            return fromDotenv;
        }

        String fromSystem = System.getenv(envKey.getKey());
        if (fromSystem != null && !fromSystem.isBlank()) {
            return fromSystem;
        }

        return envKey.getDefaultValue();
    }
}