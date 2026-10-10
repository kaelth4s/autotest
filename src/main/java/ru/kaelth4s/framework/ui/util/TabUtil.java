package ru.kaelth4s.framework.ui.util;

import java.util.Set;
import java.util.Stack;

import static aquality.selenium.browser.AqualityServices.getBrowser;
import static aquality.selenium.browser.AqualityServices.getLogger;
import static ru.kaelth4s.framework.ui.util.WaitUtil.waitNewWindow;

public final class TabUtil {
    private static Stack<String> focusHistory;

    private TabUtil() {
    }

    public static void init() {
        focusHistory = new Stack<>();
        waitNewAndSwitch(0);
    }

    public static void waitNewAndSwitch(int oldTabsCount) {
        getLogger().info("Waiting for a new tab and switching to it");
        waitNewWindow(oldTabsCount);
        Set<String> handles = getWindowHandles();
        for (String handle : handles) {
            if (!focusHistory.contains(handle)) {
                getBrowser().getDriver().switchTo().window(handle);
                focusHistory.push(handle);
                return;
            }
        }
        throw new IllegalStateException("New tab not found");
    }

    public static void closeCurrent() {
        getLogger().info("The current tab closes");
        getBrowser().getDriver().close();
        focusHistory.pop();
        getBrowser().getDriver().switchTo().window(focusHistory.peek());
    }

    public static int getTabsCount() {
        getLogger().info("Getting the number of tabs");
        return getBrowser().getDriver().getWindowHandles().size();
    }

    public static void refreshTab() {
        getBrowser().getDriver().navigate().refresh();
    }

    private static Set<String> getWindowHandles() {
        getLogger().info("Getting window descriptors");
        return getBrowser().getDriver().getWindowHandles();
    }
}