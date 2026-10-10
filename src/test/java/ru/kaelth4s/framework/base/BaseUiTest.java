package ru.kaelth4s.framework.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.kaelth4s.framework.ui.util.TabUtil;

import static aquality.selenium.browser.AqualityServices.getBrowser;
import static aquality.selenium.browser.AqualityServices.getLogger;

public abstract class BaseUiTest {
    @BeforeEach
    void setUp() {
        getLogger().info("New test case has started");
        getBrowser().maximize();
        TabUtil.init();
    }

    @AfterEach
    void tearDown() {
        getLogger().info("The test case has finished");
        getBrowser().quit();
    }
}