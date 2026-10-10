package ru.kaelth4s.framework.element;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kaelth4s.framework.base.BaseElement;
import ru.kaelth4s.framework.enums.Attribute;

import static ru.kaelth4s.framework.util.ActionUtil.scrollToElementLocatedBy;
import static ru.kaelth4s.framework.util.WaitUtil.waitVisible;

public class Input extends BaseElement {
    private static final Logger log = LoggerFactory.getLogger(Input.class);

    public Input(By locator, String elementName) {
        super(locator, elementName);
    }

    public void type(String value) {
        log.info("Entering the text '{}' in the input field of the {} element", value, elementName);
        scrollToElementLocatedBy(locator);
        waitVisible(locator).sendKeys(value);
    }

    public String getValue() {
        return getAttribute(Attribute.VALUE);
    }
}