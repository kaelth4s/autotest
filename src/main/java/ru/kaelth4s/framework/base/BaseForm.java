package ru.kaelth4s.framework.base;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kaelth4s.framework.element.Button;

import static ru.kaelth4s.framework.util.WaitUtil.*;

public abstract class BaseForm {
    private static final Logger log = LoggerFactory.getLogger(BaseForm.class);
    private final Button uniqueElement;

    private final String formName;

    public BaseForm(By uniqueElementLocator, String formName) {
        uniqueElement = new Button(uniqueElementLocator, String.format("Unique Element of %s", formName));
        this.formName = formName;
    }

    public boolean isDisplayed() {
        log.info("Check that the form to be visible '{}'", formName);
        try {
            waitVisible(uniqueElement.locator);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}