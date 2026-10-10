package ru.kaelth4s.tests.auth.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthEnvironmentKey {
    CLIENT_ID("client_id"),
    YANDEX_LOGIN("yandex_login"),
    YANDEX_PASSWORD("yandex_password");

    private final String key;
}
