package ru.kaelth4s.tests.test;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kaelth4s.framework.base.BaseTest;
import ru.kaelth4s.framework.util.TabUtil;
import ru.kaelth4s.tests.enums.Category;
import ru.kaelth4s.tests.page.*;

import static org.assertj.core.api.Assertions.assertThat;

public class HandlesTest extends BaseTest {
    private static final Logger log = LoggerFactory.getLogger(HandlesTest.class);

    @Test
    void testHandles() {
        log.info("Handles test has been launched");
        log.info("Step 1. Go to the main page");
        MainPage mainPage = new MainPage();

        assertThat(mainPage.isDisplayed())
                .as("The main page is open")
                .isTrue();

        log.info("Step 2.Click on the Alerts, Frame & Windows button. On the page that opens, click Browser Windows in the left menu.");

        mainPage.clickAlertsFrameWindowsButton();
        CategoryCardPage categoryCardPage = new CategoryCardPage();
        categoryCardPage.getLeftPanelForm().clickCategory(Category.BROWSER_WINDOWS);
        BrowserWindowsPage browserWindowsPage = new BrowserWindowsPage();

        assertThat(browserWindowsPage.isDisplayed())
                .as("The Browser Windows form page is open")
                .isTrue();

        log.info("Step 3. Click on the New Tab button");

        int oldTabCount = TabUtil.getTabsCount();
        browserWindowsPage.clickNewTabButton();
        String newTabHandle = TabUtil.waitNewAndSwitch(oldTabCount);
        SamplePage samplePage = new SamplePage();

        assertThat(TabUtil.getCurrentTabHandle())
                .as("A new tab has been opened")
                .isEqualTo(newTabHandle);
        assertThat(samplePage.isDisplayed())
                .as("The Sample page is open")
                .isTrue();

        log.info("Step 4. Close the current tab");

        TabUtil.closeCurrent();
        assertThat(browserWindowsPage.isDisplayed())
                .as("The Browser Windows form page is open")
                .isTrue();

        log.info("Step 5. In the left menu, select Elements then Links");

        browserWindowsPage.getLeftPanelForm().clickCategory(Category.LINKS);
        LinksPage linksPage = new LinksPage();

        assertThat(linksPage.isDisplayed())
                .as("The Links form page is open")
                .isTrue();

        log.info("Step 6. Click on the Home link");

        oldTabCount = TabUtil.getTabsCount();
        linksPage.clickHomeButton();
        newTabHandle = TabUtil.waitNewAndSwitch(oldTabCount);
        mainPage = new MainPage();

        assertThat(TabUtil.getCurrentTabHandle().equals(newTabHandle) && mainPage.isDisplayed())
                .as("A new tab with the main page has been opened")
                .isTrue();

        log.info("Step 7. Switch to the previous tab");

        TabUtil.switchToPrevious();

        assertThat(linksPage.isDisplayed())
                .as("The Links form page is open")
                .isTrue();
    }
}