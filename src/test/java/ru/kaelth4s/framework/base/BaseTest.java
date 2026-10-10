package ru.kaelth4s.framework.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static aquality.selenium.browser.AqualityServices.getLogger;

public abstract class BaseTest {
    @BeforeEach
    void setUp() {
        getLogger().info("New test case has started");
    }

    @AfterEach
    void tearDown() {
        getLogger().info("The test case has finished");
    }
}