package ru.kaelth4s.framework.element;

import org.openqa.selenium.By;
import ru.kaelth4s.framework.base.BaseElement;

public class Button extends BaseElement {
    public Button(By locator, String elementName) {
        super(locator, elementName);
    }
}