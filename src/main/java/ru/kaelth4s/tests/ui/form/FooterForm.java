package ru.kaelth4s.tests.ui.form;

import aquality.selenium.elements.interfaces.ILabel;
import aquality.selenium.forms.Form;
import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.ByChained;

public class FooterForm extends Form {
    private final static String VERSION_END_TEMPLATE = ": %s";

    private final ILabel footerText = getElementFactory()
            .getLabel(new ByChained(this.getLocator(), By.className("footer-text")), "Footer Text");

    public FooterForm() {
        super(By.className("footer"), "Footer");
    }

    public boolean isVersionEqual(String version) {
        return footerText.getText().strip().endsWith(VERSION_END_TEMPLATE.formatted(version));
    }
}