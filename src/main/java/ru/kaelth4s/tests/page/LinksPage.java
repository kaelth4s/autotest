package ru.kaelth4s.tests.page;

import org.openqa.selenium.By;
import ru.kaelth4s.framework.base.BaseCategoryForm;
import ru.kaelth4s.framework.element.Button;

public class LinksPage extends BaseCategoryForm {
    private final Button homeButton = new Button(By.id("simpleLink"), "Home Button");

    public LinksPage() {
        super(By.id("linkWrapper"), "Links Page");
    }

    public void clickHomeButton() {
        homeButton.click();
    }
}