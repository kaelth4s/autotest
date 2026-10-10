package ru.kaelth4s.tests.page;

import org.openqa.selenium.By;
import ru.kaelth4s.framework.base.BaseCategoryForm;
import ru.kaelth4s.framework.element.Button;

public class AlertsPage extends BaseCategoryForm {
    private final Button seeAlertButton = new Button(By.id("alertButton"), "See Alert Button");
    private final Button confirmAlertButton = new Button(By.id("confirmButton"), "Confirm Alert Button");
    private final Button confirmResultText = new Button(By.id("confirmResult"), "Confirm Result Button");
    private final Button promptAlertButton = new Button(By.id("promtButton"), "Prompt Alert Button");
    private final Button promptResultText = new Button(By.id("promptResult"), "Prompt Result Button");

    public AlertsPage() {
        super(By.id("javascriptAlertsWrapper"), "Alerts Page");
    }

    public void clickSeeAlertButton() {
        seeAlertButton.click();
    }

    public void clickConfirmAlertButton() {
        confirmAlertButton.click();
    }

    public void clickPromptAlertButton() {
        promptAlertButton.click();
    }

    public String waitAndGetConfirmResultText() {
        return confirmResultText.getVisibleText();
    }

    public String getPromptResultText() {
        return promptResultText.getAllText();
    }
}