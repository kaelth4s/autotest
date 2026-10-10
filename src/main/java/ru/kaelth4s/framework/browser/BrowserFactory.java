package ru.kaelth4s.framework.browser;

import org.openqa.selenium.WebDriver;

public interface BrowserFactory {
    WebDriver setupDriver();
}