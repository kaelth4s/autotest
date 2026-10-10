package ru.kaelth4s.framework.browser;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import ru.kaelth4s.framework.dto.BrowserOptions;
import ru.kaelth4s.framework.manager.ConfigManager;
import java.util.Map;

public class ChromeBrowserFactory implements BrowserFactory {
    @Override
    public WebDriver setupDriver() {
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver(buildChromeOptions());
    }

    private ChromeOptions buildChromeOptions() {
        BrowserOptions browserOptions = ConfigManager.getConfigData().browserOptions();
        ChromeOptions chromeOptions = new ChromeOptions();

        chromeOptions.addArguments(String.format("--lang=%s", browserOptions.interfaceLanguage()));
        chromeOptions.setExperimentalOption("prefs", Map.of("intl.accept_languages", browserOptions.acceptLanguage()));
        if (browserOptions.useIncognito())
            chromeOptions.addArguments("--incognito");
        if (browserOptions.startMaximized())
            chromeOptions.addArguments("--start-maximized");
        return chromeOptions;
    }
}