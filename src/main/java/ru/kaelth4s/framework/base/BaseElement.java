package ru.kaelth4s.framework.base;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kaelth4s.framework.enums.Attribute;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static ru.kaelth4s.framework.util.ActionUtil.scrollToElementLocatedBy;
import static ru.kaelth4s.framework.util.WaitUtil.*;
import static ru.kaelth4s.framework.manager.DriverManager.getDriver;

public abstract class BaseElement {
    private static final Logger log = LoggerFactory.getLogger(BaseElement.class);
    protected final String elementName;
    protected final By locator;

    public BaseElement(By locator, String elementName) {
        this.locator = locator;
        this.elementName = elementName;
    }

    public void click() {
        log.info("Click on the '{}' element", elementName);
        scrollToElementLocatedBy(locator);
        waitClickable(locator).click();
    }

    public String getVisibleText() {
        log.info("Getting visible text from the '{}' element", elementName);
        return waitVisible(locator).getText();
    }

    public String getAllText() {
        log.info("Getting the full text contained in the '{}' element", elementName);
        return getAttribute("textContent");
    }

    public String getAttribute(String attributeName) {
        log.info("Getting the '{}' attribute from the '{}' element", attributeName, elementName);
        return waitVisible(locator).getAttribute(attributeName);
    }

    public String getAttribute(Attribute attribute) {
        log.info("Getting the '{}' attribute from the '{}' element", attribute.getName(), elementName);
        return waitVisible(locator).getAttribute(attribute.getName());
    }

    public <T extends BaseElement> T getChild(By childLocator, String childName, Class<T> tClass) {
        ByChained fullLocator = new ByChained(locator, childLocator);
        waitVisible(fullLocator);
        T instance;
        try {
            instance = createInstance(fullLocator, childName, tClass);
        } catch (InvocationTargetException | InstantiationException |
                 IllegalAccessException    | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return instance;
    }

    protected WebElement findElement() {
        return getDriver().findElement(locator);
    }

    private <T extends BaseElement> T createInstance(By locator, String elementName, Class<T> elementClass)
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException
    {
            Constructor<T> constructor = elementClass.getDeclaredConstructor(By.class, String.class);
            return constructor.newInstance(locator, elementName);
    }
}