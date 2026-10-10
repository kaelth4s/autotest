package ru.kaelth4s.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

import static ru.kaelth4s.framework.manager.DriverManager.getDriver;
import static ru.kaelth4s.framework.util.WaitUtil.waitNewWindow;

public class TabUtil {
    private static Stack<String> focusHistory;
    private static final Logger log = LoggerFactory.getLogger(TabUtil.class);

    private TabUtil() {}

    public static void init() {
        focusHistory = new Stack<>();
        focusHistory.push(waitNewAndSwitch(0));
    }

    public static String waitNewAndSwitch(int oldTabsCount) {
        log.info("Waiting for a new tab and switching to it");
        waitNewWindow(oldTabsCount);
        Set<String> handles = getWindowHandles();
        for (String handle : handles) {
            if (!focusHistory.contains(handle)) {
                getDriver().switchTo().window(handle);
                focusHistory.push(handle);

                return handle;
            }
        }

        throw new IllegalStateException("New tab not found");
    }

    public static void switchToPrevious() {
        log.info("Switching to the previous tab");
        String currentTabHandle = focusHistory.pop();
        String previousTabHandle = focusHistory.peek();

        focusHistory.push(currentTabHandle);
        switchToTab(previousTabHandle);
    }

    public static void switchToTab(String targetTabHandle) {
        log.info("Switching to the tab with the '{}' handle", targetTabHandle);
        if (targetTabHandle == null)
            throw new IllegalArgumentException(String.format("Tab '%s' was not found!", targetTabHandle));

        getDriver().switchTo().window(targetTabHandle);

        focusHistory.remove(targetTabHandle);
        focusHistory.push(targetTabHandle);
    }

    public static void closeCurrent() {
        log.info("The current tab closes");
        getDriver().close();
        focusHistory.pop();

        getDriver().switchTo().window(focusHistory.peek());
    }

    public static String getCurrentTabHandle() {
        log.info("Getting the handle of the current tab");
        return focusHistory.peek();
    }

    public static int getTabsCount() {
        log.info("Getting the number of tabs");
        return getDriver().getWindowHandles().size();
    }

    private static Set<String> getWindowHandles() {
        log.info("Getting window descriptors");
        return getDriver().getWindowHandles();
    }
}