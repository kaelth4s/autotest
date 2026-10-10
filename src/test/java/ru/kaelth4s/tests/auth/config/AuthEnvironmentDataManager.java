package ru.kaelth4s.tests.auth.config;

import io.github.cdimascio.dotenv.Dotenv;
import ru.kaelth4s.framework.util.JsonUtil;
import ru.kaelth4s.tests.auth.dto.AuthEnvironmentData;
import ru.kaelth4s.tests.auth.enums.AuthEnvironmentKey;

public final class AuthEnvironmentDataManager {
    private static final Dotenv DOTENV = Dotenv.configure().ignoreIfMissing().load();
    private static final String AUTH_ENVIRONMENT_DATA_RESOURCE_PATH = "auth/environment.json";
    private static final AuthEnvironmentData AUTH_ENVIRONMENT_DATA = load();

    private AuthEnvironmentDataManager() {
    }

    public static AuthEnvironmentData getAuthEnvironmentData() {
        return AUTH_ENVIRONMENT_DATA;
    }

    public static String getAuthEnvKey(AuthEnvironmentKey authEnvironmentKey) {
        return DOTENV.get(authEnvironmentKey.getKey());
    }

    private static AuthEnvironmentData load() {
        AuthEnvironmentData json = JsonUtil.mapFromResourcesJson(AUTH_ENVIRONMENT_DATA_RESOURCE_PATH, AuthEnvironmentData.class);
        return AuthEnvironmentData.from(json);
    }
}