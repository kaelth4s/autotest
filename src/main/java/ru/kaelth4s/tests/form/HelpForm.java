package ru.kaelth4s.tests.form;

import aquality.selenium.elements.interfaces.IButton;
import aquality.selenium.forms.Form;
import org.openqa.selenium.By;

public class HelpForm extends Form {
    private final IButton hideHelpFormButton = getElementFactory()
            .getButton(By.className("help-form__send-to-bottom-button"), "Send To Bottom");

    public HelpForm() {
        super(By.className("help-form__title"), "Help Form");
    }

    public void hideHelpForm() {
        hideHelpFormButton.click();
    }
}