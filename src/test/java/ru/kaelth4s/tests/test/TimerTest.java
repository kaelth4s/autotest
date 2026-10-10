package ru.kaelth4s.tests.test;

import org.junit.jupiter.api.Test;
import ru.kaelth4s.framework.base.BaseTest;
import ru.kaelth4s.tests.page.MainPage;
import ru.kaelth4s.tests.page.card.LoginCardPage;

import java.time.LocalTime;

import static aquality.selenium.browser.AqualityServices.getLogger;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.kaelth4s.tests.config.TestDataManager.getTestData;

public class TimerTest extends BaseTest {
    @Test
    void shouldStartTimerFromExpectedValue() {
        getLogger().info("Timer test case has started");

        getLogger().info("Step 1. Go to the home page");

        MainPage mainPage = new MainPage();

        assertThat(mainPage.state().waitForDisplayed())
                .as("Home page is opened")
                .isTrue();

        getLogger().info("Step 2. Go to the card one page");

        mainPage.clickStartLink();
        LoginCardPage loginCardPage = new LoginCardPage();

        assertThat(loginCardPage.state().waitForDisplayed())
                .as("Card one page is opened")
                .isTrue();

        getLogger().info(String.format("Step 3. Timer start his work from %s", getTestData().timerTestData().expectedTimerStartTime()));

        LocalTime timerTime = loginCardPage.getTimerTime();
        LocalTime expectedTime = getTestData().timerTestData().expectedTimerStartTime();

        assertThat(timerTime)
                .as(String.format("Timer start his work from ~%s", expectedTime))
                .isEqualTo(expectedTime);
    }
}