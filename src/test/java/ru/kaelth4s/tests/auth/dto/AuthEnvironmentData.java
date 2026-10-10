package ru.kaelth4s.tests.auth.dto;

import ru.kaelth4s.tests.auth.enums.AuthEnvironmentKey;

import static ru.kaelth4s.tests.auth.config.AuthEnvironmentDataManager.getAuthEnvKey;

public record AuthEnvironmentData(
        String baseUrl
) {
    public static AuthEnvironmentData from(AuthEnvironmentData json) {
        return new AuthEnvironmentData(
                json.baseUrl()
                        .replace("{%s}".formatted(AuthEnvironmentKey.CLIENT_ID.getKey()), resolve(AuthEnvironmentKey.CLIENT_ID))
        );
    }

    private static String resolve(AuthEnvironmentKey envKey) {
        String fromDotenv = getAuthEnvKey(envKey);
        if (fromDotenv != null && !fromDotenv.isBlank()) {
            return fromDotenv;
        }

        String fromSystem = System.getenv(envKey.getKey());
        if (fromSystem != null && !fromSystem.isBlank()) {
            return fromSystem;
        }

        throw new IllegalStateException("Cannot resolve environment key %s".formatted(envKey));
    }
}