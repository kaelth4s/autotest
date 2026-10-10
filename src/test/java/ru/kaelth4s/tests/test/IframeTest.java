package ru.kaelth4s.tests.test;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kaelth4s.framework.base.BaseTest;
import ru.kaelth4s.tests.enums.Category;
import ru.kaelth4s.tests.page.CategoryCardPage;
import ru.kaelth4s.tests.page.FramesPage;
import ru.kaelth4s.tests.page.MainPage;
import ru.kaelth4s.tests.page.NestedFramesPage;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.kaelth4s.tests.manager.TestDataManager.getTestData;

public class IframeTest extends BaseTest {
    private static final Logger log = LoggerFactory.getLogger(IframeTest.class);

    @Test
    void testIframes() {
        log.info("Iframe test has been launched");
        log.info("Step 1. Go to the main page");
        MainPage mainPage = new MainPage();

        assertThat(mainPage.isDisplayed())
                .as("The main page is open")
                .isTrue();

        log.info("Step 2. Click on the Alerts, Frame & Windows button. On the page that opens, click Nested Frames in the left menu");

        mainPage.clickAlertsFrameWindowsButton();
        CategoryCardPage alertsWindowsPage = new CategoryCardPage();
        alertsWindowsPage.getLeftPanelForm().clickCategory(Category.NESTED_FRAMES);
        NestedFramesPage nestedFramesPage = new NestedFramesPage();

        assertThat(nestedFramesPage.isDisplayed())
                .as("A page with the Nested Frames form is open")
                .isTrue();
        assertThat(nestedFramesPage.isParentFrameTextToBe(getTestData().iframeTestData().parentFrameText()))
                .as(String.format("The label '%s' is present", getTestData().iframeTestData().parentFrameText()))
                .isTrue();
        assertThat(nestedFramesPage.isChildFrameTextToBe(getTestData().iframeTestData().childFrameText()))
                .as(String.format("The label '%s' is present", getTestData().iframeTestData().childFrameText()))
                .isTrue();

        log.info("Step 3. Select Frames from the left menu");

        nestedFramesPage.getLeftPanelForm().clickCategory(Category.FRAMES);
        FramesPage framesPage = new FramesPage();

        assertThat(framesPage.isDisplayed())
                .as("A page with the Frames form is open")
                .isTrue();
        assertThat(framesPage.getFrameOneText())
                .as("The label from the upper frame corresponds to the label from the lower one")
                .isEqualTo(framesPage.getFrameTwoText());
    }
}