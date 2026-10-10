package ru.kaelth4s.browser;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import ru.kaelth4s.service.ChromeOptionsManager;

public class ChromeBrowserFactory implements BrowserFactory {
    @Override
    public WebDriver createDriver() {
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver(ChromeOptionsManager.getChromeOptions());
    }
}
