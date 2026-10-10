package ru.kaelth4s.framework.ui.util;

import static aquality.selenium.browser.AqualityServices.getBrowser;
import static aquality.selenium.browser.AqualityServices.getLogger;

public final class FrameUtil {
    private static int frameDepth = 0;

    private FrameUtil() {
    }

    public static void switchToPage() {
        getLogger().info("Switch driver focus from frame to page");
        if (frameDepth == 0) {
            throw new IllegalStateException("Driver already has focus on the page");
        }
        getBrowser().getDriver().switchTo().defaultContent();
        frameDepth = 0;
    }

    public static void switchToFrame(String frameId) {
        getLogger().info("Switch driver focus to frame with id '%s'", frameId);
        getBrowser().getDriver().switchTo().frame(frameId);
        frameDepth++;
    }
}