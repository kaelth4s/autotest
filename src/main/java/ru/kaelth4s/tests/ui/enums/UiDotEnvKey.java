package ru.kaelth4s.tests.ui.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.kaelth4s.framework.base.DotEnvKey;

@Getter
@AllArgsConstructor
public enum UiDotEnvKey implements DotEnvKey {
    USER_LOGIN("USER_LOGIN", ""),
    USER_PASSWORD("USER_PASSWORD", "");

    private final String key;
    private final String defaultValue;
}