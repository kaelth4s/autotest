package ru.kaelth4s.tests.ui.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.kaelth4s.tests.ui.enums.UiDotEnvKey;

import java.net.URI;

import static ru.kaelth4s.framework.config.DotEnvManager.resolveDotEnv;

public record UiEnvironmentData(
        String baseUrl,
        String loginAuth,
        String passwordAuth,
        String hostPart
) {
    @JsonCreator
    public static UiEnvironmentData from(
            @JsonProperty("baseUrl") String baseUrl
    ) {
        return new UiEnvironmentData(
                baseUrl,
                resolveDotEnv(UiDotEnvKey.USER_LOGIN),
                resolveDotEnv(UiDotEnvKey.USER_PASSWORD),
                URI.create(baseUrl).getHost()
        );
    }
}