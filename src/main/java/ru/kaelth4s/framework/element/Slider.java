package ru.kaelth4s.framework.element;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kaelth4s.framework.base.BaseElement;
import ru.kaelth4s.framework.enums.Attribute;

import static ru.kaelth4s.framework.util.ActionUtil.sendKey;
import static ru.kaelth4s.framework.util.WaitUtil.waitAttributeToBe;

public class Slider extends BaseElement {
    private static final Logger log = LoggerFactory.getLogger(Slider.class);

    public Slider(By locator, String elementName) {
        super(locator, elementName);
    }

    public void setValuePercent(int valuePercent) {
        log.info("Setting value of slider '{}'", elementName);

        int offsetValuePercent = valuePercent - 50;

        click();

        for (int i = 0; i < Math.abs(offsetValuePercent); i++) {
            if (offsetValuePercent < 0) sendKey(Keys.ARROW_LEFT);
            if (offsetValuePercent > 0) sendKey(Keys.ARROW_RIGHT);
        }

        waitAttributeToBe(locator, Attribute.VALUE, String.valueOf(valuePercent));
    }
}