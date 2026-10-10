package ru.kaelth4s.framework.base;

import aquality.selenium.browser.AqualityServices;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static aquality.selenium.browser.AqualityServices.getBrowser;
import static aquality.selenium.browser.AqualityServices.getLogger;
import static ru.kaelth4s.tests.config.EnvironmentDataManager.getEnvironmentData;

public abstract class BaseTest {
    @BeforeEach
    void setUp() {
        getLogger().info("New test case has started");
        getBrowser().maximize();
        getBrowser().goTo(getEnvironmentData().baseUrl());
    }

    @AfterEach
    void tearDown() {
        getLogger().info("The test case has finished");
        if (AqualityServices.isBrowserStarted()) {
            getBrowser().quit();
        }
    }
}