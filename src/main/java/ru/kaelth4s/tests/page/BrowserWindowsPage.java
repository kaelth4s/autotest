package ru.kaelth4s.tests.page;

import org.openqa.selenium.By;
import ru.kaelth4s.framework.base.BaseCategoryForm;
import ru.kaelth4s.framework.element.Button;

public class BrowserWindowsPage extends BaseCategoryForm {

    private final Button newTabButton = new Button(By.id("tabButton"), "New Tab Button");

    public BrowserWindowsPage() {
        super(By.id("browserWindows"), "Browser Windows Page");
    }

    public void clickNewTabButton() {
        newTabButton.click();
    }
}