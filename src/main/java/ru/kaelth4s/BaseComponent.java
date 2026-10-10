package ru.kaelth4s;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import ru.kaelth4s.driver.DriverManager;
import ru.kaelth4s.service.WaitManager;
import ru.kaelth4s.util.AdvancedExpectedCondition;

import java.util.List;

public class BaseComponent {

    protected BaseComponent() {
    }

    protected WebElement find(By locator) {
        return DriverManager.getDriver().findElement(locator);
    }

    protected List<WebElement> findList(By locator) {
        return DriverManager.getDriver().findElements(locator);
    }

    protected WebElement waitVisible(By locator) {
        return WaitManager.getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitPresent(By locator) {
        return WaitManager.getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    protected List<WebElement> waitPresentList(By locator) {
        return WaitManager.getWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    protected WebElement waitClickable(By locator) {
        return WaitManager.getWait().until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected WebElement waitClickable(WebElement element) {
        return WaitManager.getWait().until(ExpectedConditions.elementToBeClickable(element));
    }

    protected void waitTextChanged(By locator, String oldText) {
        WaitManager.getWait().until(ExpectedConditions.not(ExpectedConditions.textToBe(locator, oldText)));
    }

    protected void waitNotContainsAttribute(By locator, String attributeName) {
        WaitManager.getWait().until(AdvancedExpectedCondition.attributeDisappears(locator, attributeName));
    }

    protected void click(By locator) {
        waitClickable(locator).click();
    }

    protected void click(WebElement element) { waitClickable(element).click(); }

    protected String getOwnText(By locator) {
        return (String) ((JavascriptExecutor) DriverManager.getDriver()).executeScript(
                "var parent = arguments[0]; " +
                        "var child = parent.firstChild; " +
                        "var ret = ''; " +
                        "while(child) { " +
                        "    if(child.nodeType === Node.TEXT_NODE) " +
                        "        ret += child.textContent; " +
                        "    child = child.nextSibling; " +
                        "} " +
                        "return ret.trim();",
                waitPresent(locator)
        );
    }

    protected String getText(By locator) {
        return WaitManager.getWait().until(driver -> driver.findElement(locator).getAttribute("textContent"));
    }

    protected void scrollToElementLocated(By locator) {
        new Actions(DriverManager.getDriver())
                .scrollToElement(waitPresent(locator))
                .perform();
    }

    protected void moveToElement(By locator) {
        new Actions(DriverManager.getDriver())
                .moveToElement(waitVisible(locator))
                .perform();
    }

    protected void enterKeys(By locator, String keys) {
        WebElement element = waitVisible(locator);
        element.clear();
        element.sendKeys(keys);
    }

    protected boolean isDisplayedElementLocated(By locator) {
        return find(locator).isDisplayed();
    }

    protected boolean isPresentElementLocated(By locator) {
        return !findList(locator).isEmpty();
    }
}
