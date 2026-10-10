package ru.kaelth4s.service;


import org.openqa.selenium.chrome.ChromeOptions;
import ru.kaelth4s.dto.ChromeAdditionalOptions;

import java.util.Map;

public class ChromeOptionsManager {
    private static ChromeOptions chromeOptions;

    private ChromeOptionsManager() {}

    public static ChromeOptions getChromeOptions() {
        if (chromeOptions != null) return chromeOptions;

        chromeOptions = buildChromeOptions();

        return chromeOptions;
    }

    private static ChromeOptions buildChromeOptions() {
        ChromeOptions chromeOptions = ConfigDataManager.getConfigData().chromeConfig().chromeOptions();
        ChromeAdditionalOptions chromeAdditionalOptions = ConfigDataManager.getConfigData().chromeConfig().chromeAdditionalOptions();

        chromeOptions.addArguments("--lang=" + chromeAdditionalOptions.interfaceLanguage());
        chromeOptions.setExperimentalOption("prefs", Map.of("intl.accept_languages", chromeAdditionalOptions.acceptLanguage()));
        if (chromeAdditionalOptions.useIncognito())
            chromeOptions.addArguments("--incognito");
        if (chromeAdditionalOptions.startMaximized())
            chromeOptions.addArguments("--start-maximized");
        return chromeOptions;
    }
}
