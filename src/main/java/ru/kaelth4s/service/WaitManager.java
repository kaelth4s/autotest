package ru.kaelth4s.service;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.kaelth4s.driver.DriverManager;

import java.time.Duration;

public class WaitManager {
    private WaitManager() {}

    public static WebDriverWait getWait() {
        WebDriverWait wait = new WebDriverWait(
                DriverManager.getDriver(),
                Duration.ofSeconds(ConfigDataManager.getConfigData().explicitWaitsDuration())
        );
        wait.ignoring(StaleElementReferenceException.class);
        return wait;
    }
}
