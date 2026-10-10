package ru.kaelth4s.framework.util;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kaelth4s.framework.enums.Attribute;
import ru.kaelth4s.framework.manager.ConfigManager;

import java.time.Duration;
import java.util.List;

import static ru.kaelth4s.framework.manager.DriverManager.getDriver;
import static ru.kaelth4s.framework.util.ParserUtil.parseInt;

public class WaitUtil {
    private static final Logger log = LoggerFactory.getLogger(WaitUtil.class);
    private static final int defaultDuration = ConfigManager.getConfigData().defaultWaitDuration();

    private WaitUtil() {}

    private static WebDriverWait getDefaultWait() {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(defaultDuration));
    }

    private static WebDriverWait getCustomWait(int timeoutSeconds, int pollingMillis) {
        WebDriverWait customWait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutSeconds));
        customWait.pollingEvery(Duration.ofMillis(pollingMillis));
        return customWait;
    }

    public static WebElement waitClickable(By locator) {
        log.info("Waiting for the opportunity to click on the element located by '{}'", locator);
        return getDefaultWait()
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitVisible(By locator) {
        log.info("Waiting for the element located by '{}' to be visible", locator);
        return getDefaultWait()
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static List<WebElement> waitVisibles(By locator) {
        log.info("Waiting for the elements located by '{}' to be visible", locator);
        return getDefaultWait()
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public static boolean waitNotPresent(By locator) {
        log.info("Waiting for the element located by '{}' to be invisible", locator);
        return getDefaultWait()
                .until(ExpectedConditions.not(ExpectedConditions.presenceOfElementLocated(locator)));
    }

    public static void waitNewWindow(int oldWindowsCount) {
        log.info("Waiting for a new window to appear");
        getDefaultWait()
                .until(ExpectedConditions.numberOfWindowsToBe(oldWindowsCount + 1));
    }

    public static void waitAttributeToBe(By locator, String attributeName, String value) {
        log.info("Waiting for attribute '{}' value to be '{}' in element located by '{}'", attributeName, value, locator);
        getDefaultWait()
                .until(ExpectedConditions.attributeToBe(locator, attributeName, value));
    }


    public static void waitAttributeToBe(By locator, Attribute attribute, String value) {
        log.info("Waiting for attribute '{}' value to be '{}' in element located by '{}'", attribute.getName(), value, locator);
        getDefaultWait()
                .until(ExpectedConditions.attributeToBe(locator, attribute.getName(), value));
    }

    public static void waitNumberAttributeGreaterThanOrEqualTo(By locator, String attributeName, int minValue, int timeoutSeconds, int pollingMillis) {
        log.info("Waiting for number attribute '{}' value greater than or equal to '{}' in element located by '{}'", attributeName, minValue, locator);
        getCustomWait(timeoutSeconds, pollingMillis).until(driver -> {
            try {
                WebElement element = driver.findElement(locator);
                String attributeValue = element.getAttribute(attributeName);
                if (attributeValue == null) return false;
                int currentValue = parseInt(attributeValue);
                return currentValue >= minValue;
            } catch (NoSuchElementException e) {
                return false;
            }
        });
    }

    public static boolean waitAlertClosed() {
        log.info("Waiting for the alert to close");
        return getDefaultWait()
                .until(ExpectedConditions.not(ExpectedConditions.alertIsPresent()));
    }

    public static Alert waitAlert() {
        log.info("Waiting for the alert to appear");
        return getDefaultWait()
                .until(ExpectedConditions.alertIsPresent());
    }

    public static boolean waitTextToBe(By locator, String text) {
        log.info("Waiting for text in element located by '{}' to be '{}'", locator, text);
        return getDefaultWait()
                .until(ExpectedConditions.textToBe(locator, text));
    }
}