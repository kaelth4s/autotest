package ru.kaelth4s.tests.page;

import org.openqa.selenium.By;
import ru.kaelth4s.framework.base.BaseCategoryForm;
import ru.kaelth4s.framework.element.Button;

public class MainPage extends BaseCategoryForm {
    private final Button alertsFrameWindowsButton = new Button(By.xpath("//*[@href='/alertsWindows']"), "Alerts, Frame & Windows Button");
    private final Button elementsButton = new Button(By.xpath("//*[@href='/elements']"), "Elements Button");
    private final Button widgetsButton = new Button(By.xpath("//*[@href='/widgets']"), "Widgets Button");

    public MainPage() {
        super(By.className("home-banner"), "MainPage");
    }

    public void clickAlertsFrameWindowsButton() {
        alertsFrameWindowsButton.click();
    }

    public void clickElementsButton() {
        elementsButton.click();
    }

    public void clickWidgetsButton() {
        widgetsButton.click();
    }
}