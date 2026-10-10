package ru.kaelth4s.framework.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kaelth4s.framework.manager.ConfigManager;
import ru.kaelth4s.framework.manager.DriverManager;
import ru.kaelth4s.framework.util.TabUtil;


public abstract class BaseTest {
    private static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    @BeforeEach
    void setUp() {
        DriverManager.getDriver().get(ConfigManager.getConfigData().url());
        TabUtil.init();
    }

    @AfterEach
    void tearDown() {
        log.info("The test case has finished");
        DriverManager.quitDriver();
    }
}