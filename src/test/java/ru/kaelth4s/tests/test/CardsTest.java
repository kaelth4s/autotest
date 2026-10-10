package ru.kaelth4s.tests.test;

import org.junit.jupiter.api.Test;
import ru.kaelth4s.framework.base.BaseTest;
import ru.kaelth4s.tests.page.MainPage;
import ru.kaelth4s.tests.page.card.InterestsCardPage;
import ru.kaelth4s.tests.page.card.LoginCardPage;
import ru.kaelth4s.tests.page.card.PersonalDetailsCardPage;

import static aquality.selenium.browser.AqualityServices.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.kaelth4s.tests.config.TestDataManager.getTestData;

public class CardsTest extends BaseTest {
    @Test
    void shouldOpenCards() {
        getLogger().info("Cards Test has started");

        getLogger().info("Step 1. Go to the home page");
        MainPage mainPage = new MainPage();

        assertThat(mainPage.state().waitForDisplayed())
                .as("Home page is opened")
                .isTrue();

        getLogger().info("Step 2. Click on the start link");

        mainPage.clickStartLink();
        LoginCardPage loginCardPage = new LoginCardPage();

        assertThat(loginCardPage.state().waitForDisplayed())
                .as("Card one page is opened")
                .isTrue();

        getLogger().info("Step 3. Enter random pass, email, accept terms and conditions and click Next button");

        loginCardPage.inputEmail(getTestData().cardsTestData().emailName());
        loginCardPage.inputDomain(getTestData().cardsTestData().emailDomain());
        loginCardPage.selectRandomNationalDomain();
        loginCardPage.inputPassword(getTestData().cardsTestData().password());
        loginCardPage.acceptTermsAndConditions();
        loginCardPage.clickNextButton();
        InterestsCardPage interestsCardPage = new InterestsCardPage();

        assertThat(interestsCardPage.state().waitForDisplayed())
                .as("Card two page is opened")
                .isTrue();

        getLogger().info(String.format("Step 4. Choose %s random interests, upload an image and click Next button", getTestData().cardsTestData().interestsCount()));

        interestsCardPage.unselectAllCheckboxes();
        interestsCardPage.selectRandomInterestCheckboxes(getTestData().cardsTestData().interestsCount());
        interestsCardPage.uploadImage(getTestData().cardsTestData().imageResourcePath());
        interestsCardPage.clickNextButton();
        PersonalDetailsCardPage personalDetailsCardPage = new PersonalDetailsCardPage();

        assertThat(personalDetailsCardPage.state().waitForDisplayed())
                .as("Card three page is opened")
                .isTrue();
    }
}