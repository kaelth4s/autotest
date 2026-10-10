package ru.kaelth4s.framework.element;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kaelth4s.framework.base.BaseElement;

import static ru.kaelth4s.framework.util.WaitUtil.waitClickable;

public class Dropdown extends BaseElement {
    private static final Logger log = LoggerFactory.getLogger(Dropdown.class);

    public Dropdown(By locator, String elementName) {
        super(locator, elementName);
    }

    public void selectByVisibleText(String text) {
        log.info("Selecting the option '{}' in the drop-down list {}", text, elementName);
        Select select = getSelect();

        for (WebElement option : select.getOptions()) {
            if (option.getText().toLowerCase().equals(text)) {
                option.click();
                return;
            }
        }

        throw new NoSuchElementException(String.format("Option with text '%s' not found", text));
    }

    private Select getSelect() {
        click();
        return new Select(waitClickable(locator));
    }
}