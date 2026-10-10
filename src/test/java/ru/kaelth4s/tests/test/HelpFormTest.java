package ru.kaelth4s.tests.test;

import org.junit.jupiter.api.Test;
import ru.kaelth4s.framework.base.BaseTest;
import ru.kaelth4s.tests.page.MainPage;
import ru.kaelth4s.tests.page.card.LoginCardPage;

import static aquality.selenium.browser.AqualityServices.getLogger;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class HelpFormTest extends BaseTest {
    @Test
    void shouldHideHelpForm() {
        getLogger().info("Help form test case has started");

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

        getLogger().info("Step 3. Hide help form");

        loginCardPage.getHelpForm().hideHelpForm();

        assertThat(loginCardPage.getHelpForm().state().waitForNotDisplayed())
                .as("Help form is hidden")
                .isTrue();
    }
}