package ru.kaelth4s.tests.auth.dto;

public record AuthConfigData(
        int timeoutSFA,
        int timeoutAuthFormLoad
) {
    public static AuthConfigData from(AuthConfigData json) {
        return new AuthConfigData(
                json.timeoutSFA(),
                json.timeoutAuthFormLoad()
        );
    }
}