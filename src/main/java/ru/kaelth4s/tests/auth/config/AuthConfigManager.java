package ru.kaelth4s.tests.auth.config;

import ru.kaelth4s.framework.util.JsonUtil;
import ru.kaelth4s.tests.auth.dto.AuthConfigData;

public class AuthConfigManager {
    private static final String AUTH_ENVIRONMENT_DATA_RESOURCE_PATH = "auth/config.json";
    private static final AuthConfigData AUTH_ENVIRONMENT_DATA = load();

    private AuthConfigManager() {
    }

    public static AuthConfigData getAuthEnvironmentData() {
        return AUTH_ENVIRONMENT_DATA;
    }

    private static AuthConfigData load() {
        AuthConfigData json = JsonUtil.mapFromResourcesJson(AUTH_ENVIRONMENT_DATA_RESOURCE_PATH, AuthConfigData.class);
        return AuthConfigData.from(json);
    }
}