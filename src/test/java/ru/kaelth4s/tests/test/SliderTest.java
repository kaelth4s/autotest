package ru.kaelth4s.tests.test;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kaelth4s.framework.base.BaseTest;
import ru.kaelth4s.tests.enums.Category;
import ru.kaelth4s.tests.page.CategoryCardPage;
import ru.kaelth4s.tests.page.MainPage;
import ru.kaelth4s.tests.page.ProgressBarPage;
import ru.kaelth4s.tests.page.SliderPage;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.kaelth4s.tests.manager.TestDataManager.getTestData;

public class SliderTest extends BaseTest {
    private static final Logger log = LoggerFactory.getLogger(SliderTest.class);

    @Test
    void testSlider() {
        log.info("SliderTest has launched");
        log.info("Step 1. Go to the main page");
        MainPage mainPage = new MainPage();

        assertThat(mainPage.isDisplayed())
                .as("The main page is open")
                .isTrue();

        log.info("Step 2. Click on the Widgets button. In the left menu select Slider link");

        mainPage.clickWidgetsButton();
        CategoryCardPage categoryCardPage = new CategoryCardPage();
        categoryCardPage.getLeftPanelForm().clickCategory(Category.SLIDER);
        SliderPage sliderPage = new SliderPage();

        assertThat(sliderPage.isDisplayed())
                .as("The Slider page is open")
                .isTrue();

        log.info("Step 3. Set to slider random correct value");

        sliderPage.setSliverValue(getTestData().sliderTestData().sliderValuePercent());

        assertThat(sliderPage.getSliderInputValue())
                .as("Slider value is equal to random generated value")
                .isEqualTo(getTestData().sliderTestData().sliderValuePercent());

        log.info("Step 4. In the left menu click Progress Bar link");

        sliderPage.getLeftPanelForm().clickCategory(Category.PROGRESS_BAR);
        ProgressBarPage progressBarPage = new ProgressBarPage();

        assertThat(progressBarPage.isDisplayed())
                .as("The Progress Bar page is open")
                .isTrue();

        log.info("Step 5. Click on the start button");

        progressBarPage.clickStartButton();

        log.info("Step 6. Click on the stop button when progress bar value is equal engineer age");

        progressBarPage.waitForProgressBarValueGreaterThan(getTestData().sliderTestData().engineerAge() - getTestData().sliderTestData().error());

        progressBarPage.clickStopButton();

        assertThat(progressBarPage.getProgressBarValue())
                .as(String.format("Progress bar value is equal to engineer age (error is less than '%s'%%)", getTestData().sliderTestData().error()))
                .isLessThanOrEqualTo(getTestData().sliderTestData().engineerAge() + getTestData().sliderTestData().error());
    }
}