package ru.kaelth4s.base;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import ru.kaelth4s.dto.ConfigData;
import ru.kaelth4s.driver.DriverManager;
import ru.kaelth4s.page.StorePage;
import ru.kaelth4s.service.ConfigDataManager;

public class BaseTest {
    @BeforeEach
    public void setUp() {
        DriverManager.getDriver();
    }

    @AfterEach
    public void tearDown() {
        DriverManager.quitDriver();
    }

    protected StorePage openUrl() {
        DriverManager.getDriver().get(ConfigDataManager.getConfigData().url());
        StorePage storePage = new StorePage();

        Assertions.assertThat(storePage.isPageOpened())
                .as("Проверка, что главная страница открыта")
                .isTrue();

        return storePage;
    }

}
