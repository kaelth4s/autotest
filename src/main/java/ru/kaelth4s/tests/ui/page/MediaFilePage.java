package ru.kaelth4s.tests.ui.page;

import aquality.selenium.elements.interfaces.IButton;
import aquality.selenium.forms.Form;
import org.openqa.selenium.By;

public class MediaFilePage extends Form {
    private final IButton closeButton = getElementFactory()
            .getButton(By.className("slider__button_close"), "Close");
    private final IButton image = getElementFactory()
            .getButton(By.className("scalable-preview__image"), "Image");

    public MediaFilePage() {
        super(By.className("scalable-preview__image"), "Media File Page");
    }

    public void closeMedia() {
        closeButton.click();
    }
}