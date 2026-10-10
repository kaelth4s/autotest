package ru.kaelth4s.tests.ui.page;

import aquality.selenium.forms.Form;
import lombok.Getter;
import org.openqa.selenium.By;
import ru.kaelth4s.tests.ui.form.TestTableForm;

@Getter
public class ProjectPage extends Form {
    private final TestTableForm testTableForm = new TestTableForm();

    public ProjectPage() {
        super(By.id("pie"), "Project Page");
    }
}