package ru.kaelth4s.util;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class AdvancedExpectedCondition {

    public static ExpectedCondition<Boolean> attributeDisappears(By locator, String attributeName) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                String value = driver.findElement(locator).getAttribute(attributeName);
                return value == null || value.isEmpty();
            }

            @Override
            public String toString() {
                return String.format("Ожидание исчезновения атрибута '%s' у элемента %s", attributeName, locator);
            }
        };
    }
}
