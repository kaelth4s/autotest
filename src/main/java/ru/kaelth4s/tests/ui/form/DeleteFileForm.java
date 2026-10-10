package ru.kaelth4s.tests.ui.form;

import aquality.selenium.elements.interfaces.IButton;
import aquality.selenium.forms.Form;
import org.openqa.selenium.By;

public class DeleteFileForm extends Form {
    private final IButton deleteButton = getElementFactory()
            .getButton(By.xpath("//*[contains(@data-testid,'submit-button')]"), "Delete");

    public DeleteFileForm() {
        super(By.className("Orb-Modal2-Content"), "Delete File Form");
    }

    public void deleteFile() {
        deleteButton.click();
    }
}