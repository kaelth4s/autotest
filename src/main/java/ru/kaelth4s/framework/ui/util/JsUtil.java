package ru.kaelth4s.framework.ui.util;

import ru.kaelth4s.framework.ui.base.JsScript;

import static aquality.selenium.browser.AqualityServices.getBrowser;
import static ru.kaelth4s.framework.util.FileUtil.readResource;

public final class JsUtil {
    private JsUtil() {
    }

    public static void execute(JsScript jsScript) {
        String script = readResource(jsScript.getPath());
        getBrowser().executeScript(script);
    }

    public static void execute(JsScript jsScript, Object... args) {
        String script = readResource(jsScript.getPath());
        getBrowser().executeScript(script, args);
    }
}