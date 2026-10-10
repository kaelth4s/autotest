package ru.kaelth4s.framework.manager;

import org.openqa.selenium.WebDriver;
import ru.kaelth4s.framework.browser.Browser;
import ru.kaelth4s.framework.browser.BrowserFactory;
import ru.kaelth4s.framework.browser.ChromeBrowserFactory;
import ru.kaelth4s.framework.browser.FirefoxBrowserFactory;

public class DriverManager {
    private static WebDriver driver;

    private DriverManager() {}

    public static WebDriver getDriver() {
        if (driver != null) return driver;

        BrowserFactory browserFactory;
        Browser browser = ConfigManager.getConfigData().browser();
        browserFactory = switch (browser) {
            case FIREFOX -> new FirefoxBrowserFactory();
            case CHROME -> new ChromeBrowserFactory();
            case UNKNOWN -> throw new IllegalArgumentException("Unknown browser");
        };

        driver = browserFactory.setupDriver();
        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}