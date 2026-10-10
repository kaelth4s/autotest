package ru.kaelth4s.driver;

import org.openqa.selenium.WebDriver;
import ru.kaelth4s.browser.BrowserFactory;
import ru.kaelth4s.browser.ChromeBrowserFactory;
import ru.kaelth4s.browser.FirefoxBrowserFactory;
import ru.kaelth4s.service.ConfigDataManager;

public class DriverManager {
    private static WebDriver driver;

    private DriverManager() {}

    public static WebDriver getDriver() {
        if (driver != null) return driver;

        BrowserFactory factory;
        String browserName = ConfigDataManager.getConfigData().browserName().toLowerCase();
        factory = switch (browserName) {
            case "firefox" -> new FirefoxBrowserFactory();
            case "chrome" -> new ChromeBrowserFactory();
            default ->
                    throw new IllegalArgumentException("Неправильный config.json. Браузера " + browserName + " не существует!");
        };
        driver = factory.createDriver();
        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
