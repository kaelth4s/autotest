package ru.kaelth4s.framework.browser;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum Browser {
    CHROME,
    FIREFOX,
    @JsonEnumDefaultValue
    UNKNOWN
}