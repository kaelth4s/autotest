package ru.kaelth4s.tests.test;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kaelth4s.framework.base.BaseTest;
import ru.kaelth4s.framework.util.AlertUtil;
import ru.kaelth4s.tests.enums.Category;
import ru.kaelth4s.tests.page.AlertsPage;
import ru.kaelth4s.tests.page.CategoryCardPage;
import ru.kaelth4s.tests.page.MainPage;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.kaelth4s.tests.manager.TestDataManager.getTestData;

public class AlertsTest extends BaseTest {
    private static final Logger log = LoggerFactory.getLogger(AlertsTest.class);

    @Test
    void testAlerts() {
        log.info("Alert test has been launched");
        log.info("Step 1. Go to the main page");
        MainPage mainPage = new MainPage();

        assertThat(mainPage.isDisplayed())
                .as("The main page is opened")
                .isTrue();

        log.info("Step 2.Click on the Alerts, Frame & Windows button. On the page that opens, click Alerts in the left menu.");

        mainPage.clickAlertsFrameWindowsButton();
        CategoryCardPage categoryCardPage = new CategoryCardPage();
        categoryCardPage.getLeftPanelForm().clickCategory(Category.ALERTS);
        AlertsPage alertsPage = new AlertsPage();

        assertThat(alertsPage.isDisplayed())
                .as("The Alerts form appeared")
                .isTrue();

        log.info("Step 3. Click on the button 'Click Button to see alert'");

        alertsPage.clickSeeAlertButton();

        assertThat(AlertUtil.getAlertText())
                .as(String.format("The alert has been opened with the text '%s'", getTestData().alertsTestData().seeAlertText()))
                .isEqualTo(getTestData().alertsTestData().seeAlertText());

        log.info("Step 4. Click on the OK button");

        AlertUtil.acceptAlert();

        assertThat(AlertUtil.isAlertClosed())
                .as("The alert has closed")
                .isTrue();

        log.info("Step 5. Click on the button 'On button click, confirm box will appear'");

        alertsPage.clickConfirmAlertButton();

        assertThat(AlertUtil.getAlertText())
                .as(String.format("The alert has been opened with the text '%s'", getTestData().alertsTestData().confirmBoxAlertText()))
                .isEqualTo(getTestData().alertsTestData().confirmBoxAlertText());

        log.info("Step 6. Click on the OK button");

        AlertUtil.acceptAlert();

        assertThat(AlertUtil.isAlertClosed())
                .as ("The alert has closed")
                .isTrue();
        assertThat(alertsPage.waitAndGetConfirmResultText())
                .as(String.format("The inscription '%s' has been appeared near the button", getTestData().alertsTestData().confirmInscriptionText()))
                .isEqualTo(getTestData().alertsTestData().confirmInscriptionText());

        log.info("Step 7. Click on the button 'On button click, prompt box will appear'");

        alertsPage.clickPromptAlertButton();

        assertThat(AlertUtil.getAlertText())
                .as(String.format("The alert has been appeared with the text '%s'", getTestData().alertsTestData().promptBoxAlertText()))
                .isEqualTo(getTestData().alertsTestData().promptBoxAlertText());

        log.info("Step 8. Input random generated text, click on the OK button");

        String inputText = getTestData().alertsTestData().inputText();
        AlertUtil.inputAlert(inputText);
        AlertUtil.acceptAlert();

        assertThat(AlertUtil.isAlertClosed())
                .as("The alert has closed")
                .isTrue();
        assertThat(alertsPage.getPromptResultText().contains(inputText))
                .as("The appeared text is equal to typed into alert text")
                .isTrue();
    }
}