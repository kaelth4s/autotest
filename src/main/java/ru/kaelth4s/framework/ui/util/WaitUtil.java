package ru.kaelth4s.framework.ui.util;

import org.openqa.selenium.support.ui.ExpectedConditions;

import static aquality.selenium.browser.AqualityServices.*;

public final class WaitUtil {
    private WaitUtil() {
    }

    public static void waitNewWindow(int oldWindowsCount) {
        getLogger().info("Waiting for a new window to appear");

        var condition = ExpectedConditions.numberOfWindowsToBe(oldWindowsCount + 1);

        getConditionalWait().waitFor(() ->
                condition.apply(getBrowser().getDriver())
        );
    }
}