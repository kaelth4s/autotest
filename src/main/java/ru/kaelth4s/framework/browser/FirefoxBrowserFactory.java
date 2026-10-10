package ru.kaelth4s.framework.browser;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import ru.kaelth4s.framework.dto.BrowserOptions;
import ru.kaelth4s.framework.manager.ConfigManager;

public class FirefoxBrowserFactory implements BrowserFactory {
    @Override
    public WebDriver setupDriver() {
        WebDriverManager.firefoxdriver().setup();
        WebDriver firefoxDriver = new FirefoxDriver(buildFirefoxOptions());

        if (ConfigManager.getConfigData().browserOptions().startMaximized())
            firefoxDriver.manage().window().maximize();

        return firefoxDriver;
    }

    private FirefoxOptions buildFirefoxOptions() {
        BrowserOptions browserOptions = ConfigManager.getConfigData().browserOptions();
        FirefoxProfile firefoxProfile = new FirefoxProfile();

        firefoxProfile.setPreference("intl.accept_languages", browserOptions.acceptLanguage());
        firefoxProfile.setPreference("general.useragent.locale", browserOptions.interfaceLanguage());

        if (browserOptions.useIncognito())
            firefoxProfile.setPreference("browser.privatebrowsing.autostart", true);

        return new FirefoxOptions(firefoxProfile);
    }
}