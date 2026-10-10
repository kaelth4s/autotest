package ru.kaelth4s.tests.form;

import aquality.selenium.elements.interfaces.IButton;
import aquality.selenium.forms.Form;
import org.openqa.selenium.By;

public class CookiesForm extends Form {
    private final IButton acceptCookiesButton = getElementFactory()
            .getButton(By.xpath("//*[contains(@class,'cookies')]//button[not(contains(@class,'cookies'))]"), "Accept Cookies");

    public CookiesForm() {
        super(By.className("cookies__message"), "Cookies Form");
    }

    public void acceptCookies() {
        acceptCookiesButton.click();
    }
}