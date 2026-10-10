package ru.kaelth4s.tests.ui.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.kaelth4s.framework.ui.base.JsScript;

@Getter
@AllArgsConstructor
public enum TestJs implements JsScript {
    CLOSE_POP_UP("js/closePopUp.js");

    private final String path;
}