package ru.kaelth4s.tests.ui.page;

import aquality.selenium.elements.interfaces.ILabel;
import aquality.selenium.forms.Form;
import org.openqa.selenium.By;

public class DocumentFilePage extends Form {
    private final ILabel documentTextContainer = getElementFactory()
            .getLabel(By.className("js-doc-page"), "Document Text");

    public DocumentFilePage() {
        super(By.className("js-doc-page"), "Document File Page");
    }

    public String getDocumentText() {
        return documentTextContainer.getText();
    }
}