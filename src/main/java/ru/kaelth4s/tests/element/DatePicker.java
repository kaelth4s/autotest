package ru.kaelth4s.tests.element;

import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kaelth4s.framework.base.BaseElement;
import ru.kaelth4s.framework.element.Button;
import ru.kaelth4s.framework.element.Dropdown;

import java.time.LocalDate;

public class DatePicker extends BaseElement {
    private static final Logger log = LoggerFactory.getLogger(DatePicker.class);
    private final Dropdown monthDropdown;
    private final Dropdown yearDropdown;
    private final Button daysContainer;

    public DatePicker(By locator, String elementName) {
        super(locator, elementName);
        monthDropdown = new Dropdown(new ByChained(locator, By.xpath("//*[contains(@class, 'month-select')]")), String.format("%s Month Dropdown", elementName));
        yearDropdown = new Dropdown(new ByChained(locator, By.xpath("//*[contains(@class, 'year-select')]")), String.format("%s Year Dropdown", elementName));
        daysContainer = getChild(
                By.xpath(".//*[contains(@class,'react-datepicker__month')]"),
                String.format("%s Table Days Container", elementName),
                Button.class
        );
    }

    public void clickDate(LocalDate date) {
        log.info("Choosing '{}' date", date);
        monthDropdown.selectByVisibleText(date.getMonth().name().toLowerCase());
        yearDropdown.selectByVisibleText(String.valueOf(date.getYear()));
        Button targetDayButton = daysContainer.getChild(
                By.xpath(String.format(".//*[contains(@class, 'react-datepicker__day') and not(contains(@class,'outside-month')) and contains(text(), '%s')]", date.getDayOfMonth())),
                String.format("%s date button", date),
                Button.class
        );
        targetDayButton.click();
    }
}