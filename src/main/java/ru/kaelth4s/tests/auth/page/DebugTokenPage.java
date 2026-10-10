package ru.kaelth4s.tests.auth.page;

import aquality.selenium.elements.interfaces.ILabel;
import aquality.selenium.forms.Form;
import org.openqa.selenium.By;

public class DebugTokenPage extends Form {
    private final ILabel debugTokenLabel = getElementFactory()
            .getLabel(By.className("verification-code-flow-token-output"), "Debug Token");

    public DebugTokenPage() {
        super(By.className("verification-code-flow-token-output"), "Debug Token Page");
    }

    public String getDebugToken() {
        return debugTokenLabel.getText();
    }
}