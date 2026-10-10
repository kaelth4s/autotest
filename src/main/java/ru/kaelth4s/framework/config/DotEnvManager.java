package ru.kaelth4s.framework.config;

import io.github.cdimascio.dotenv.Dotenv;
import ru.kaelth4s.framework.base.DotEnvKey;

public final class DotEnvManager {
    private static final Dotenv DOT_ENV = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    private DotEnvManager() {
    }

    public static String resolveDotEnv(DotEnvKey dotEnvKey) {
        String fromDotenv = DOT_ENV.get(dotEnvKey.getKey());
        if (fromDotenv != null && !fromDotenv.isBlank()) {
            return fromDotenv;
        }
        String fromSystem = System.getenv(dotEnvKey.getKey());
        if (fromSystem != null && !fromSystem.isBlank()) {
            return fromSystem;
        }
        return dotEnvKey.getDefaultValue();
    }
}