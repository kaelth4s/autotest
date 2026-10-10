package ru.kaelth4s.tests.page;

import aquality.selenium.elements.interfaces.ILink;
import aquality.selenium.forms.Form;
import org.openqa.selenium.By;

public class MainPage extends Form {
    private final ILink startLink = getElementFactory()
            .getLink(By.className("start__link"), "Start");

    public MainPage() {
        super(By.className("start__link"), "Main Page");
    }

    public void clickStartLink() {
        startLink.click();
    }
}