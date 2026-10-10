package ru.kaelth4s.framework.util;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ru.kaelth4s.framework.manager.DriverManager.getDriver;
import static ru.kaelth4s.framework.util.WaitUtil.waitVisible;

public class ActionUtil {
    private static final Logger log = LoggerFactory.getLogger(ActionUtil.class);

    private ActionUtil() {}

    public static void scrollToElementLocatedBy(By locator) {
        log.info("The page scrolls to the element located by '{}'", locator);
        new Actions(getDriver())
                .scrollToElement(waitVisible(locator))
                .perform();
    }

    public static void sendKey(Keys key) {
        new Actions(getDriver())
                .sendKeys(key)
                .perform();

    }
}