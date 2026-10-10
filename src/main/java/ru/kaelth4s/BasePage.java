package ru.kaelth4s;

import org.openqa.selenium.By;

public abstract class BasePage extends BaseComponent {

    protected BasePage() {
        ;
    }

    public boolean isPageOpened() {
        waitVisible(getUniqueElementLocator());
        return true;
    }

    protected abstract By getUniqueElementLocator();
}
