package ru.kaelth4s.tests.page;

import org.openqa.selenium.By;
import ru.kaelth4s.framework.base.BaseCategoryForm;
import ru.kaelth4s.framework.element.Input;
import ru.kaelth4s.tests.dto.Date;
import ru.kaelth4s.tests.element.DatePicker;

import java.time.LocalDate;

public class DatePickerPage extends BaseCategoryForm {
    private final Input selectDateInput = new Input(By.id("datePickerMonthYearInput"), "Select Date Input");
    private final Input dateAndTimeInput = new Input(By.id("dateAndTimePickerInput"), "Date And Time Input");

    public DatePickerPage() {
        super(By.id("datePickerMonthYearInput"), "Date Picker Page");
    }

    public Date getSelectDateInputValue() {
        return new Date(selectDateInput.getValue());
    }

    public Date getDateAndTimeInputValue() {
        return new Date(dateAndTimeInput.getValue());
    }

    public void selectDate(LocalDate date) {
        clickSelectDateInput();
        DatePicker datePicker = new DatePicker(By.className("react-datepicker__month-container"), "Date Picker");
        datePicker.clickDate(date);
    }

    private void clickSelectDateInput() {
        selectDateInput.click();
    }
}