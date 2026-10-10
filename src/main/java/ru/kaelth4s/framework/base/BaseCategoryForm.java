package ru.kaelth4s.framework.base;

import org.openqa.selenium.By;
import ru.kaelth4s.tests.form.LeftPanelForm;

public abstract class BaseCategoryForm extends BaseForm {
    private final LeftPanelForm leftPanelForm = new LeftPanelForm();

    public BaseCategoryForm(By uniqueElementLocator, String formName) {
        super(uniqueElementLocator, formName);
    }

    public LeftPanelForm getLeftPanelForm() {
        return leftPanelForm;
    }
}
