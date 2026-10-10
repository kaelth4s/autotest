package ru.kaelth4s.tests.test;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kaelth4s.framework.base.BaseTest;
import ru.kaelth4s.tests.enums.Category;
import ru.kaelth4s.tests.page.CategoryCardPage;
import ru.kaelth4s.tests.page.DatePickerPage;
import ru.kaelth4s.tests.page.MainPage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.kaelth4s.framework.util.LocalDateUtil.getNearestDay;
import static ru.kaelth4s.tests.manager.TestDataManager.getTestData;

public class DatePickerTest extends BaseTest {
    private static final Logger log = LoggerFactory.getLogger(DatePickerTest.class);

    @Test
    void testDatePicker() {
        log.info("Date Picker test launch");
        log.info("Step 1. Go to the main page");

        MainPage mainPage = new MainPage();

        assertThat(mainPage.isDisplayed())
                .as("The main page is open")
                .isTrue();

        log.info("Step 2. Click on the Widgets button. On the opened page click Date Picker button");

        mainPage.clickWidgetsButton();
        CategoryCardPage categoryCardPage = new CategoryCardPage();
        LocalDateTime now = LocalDateTime.now();
        categoryCardPage.getLeftPanelForm().clickCategory(Category.DATE_PICKER);
        DatePickerPage datePickerPage = new DatePickerPage();

        assertThat(datePickerPage.isDisplayed())
                .as("The Date Picker Page is open")
                .isTrue();
        assertThat(datePickerPage.getSelectDateInputValue().getDate())
                .as("Value in Select Date field equal current date")
                .isEqualTo(now.toLocalDate());
        assertThat(datePickerPage.getDateAndTimeInputValue().getDateTime())
                .as("Value in Date And Time field equal current date and time")
                .isEqualTo(now.truncatedTo(ChronoUnit.MINUTES));

        log.info("Step 3. Using the select data window in Data Picker field set date of nearest 29 February");

        LocalDate nearestDay = getNearestDay(getTestData().datePickerTestData().nearestDay());
        datePickerPage.selectDate(nearestDay);

        assertThat(datePickerPage.getSelectDateInputValue().getDate())
                .as("Value in Select Date field equal input value")
                .isEqualTo(nearestDay);
    }
}