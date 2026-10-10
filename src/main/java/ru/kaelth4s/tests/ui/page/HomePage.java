package ru.kaelth4s.tests.ui.page;

import aquality.selenium.elements.interfaces.IButton;
import aquality.selenium.elements.interfaces.IElement;
import aquality.selenium.forms.Form;
import lombok.Getter;
import org.openqa.selenium.By;
import ru.kaelth4s.tests.ui.form.AddProjectForm;
import ru.kaelth4s.tests.ui.form.FooterForm;

import java.util.List;

public class HomePage extends Form {
    private static final By PROJECTS_LOCATOR = By.className("list-group-item");
    private static final By ADD_PROJECT_BUTTON_LOCATOR = By.xpath("//*[contains(@data-target,'addProject')]");

    private final IButton addProjectButton = getElementFactory()
            .getButton(ADD_PROJECT_BUTTON_LOCATOR, "Add Project");

    @Getter
    private final FooterForm footerForm = new FooterForm();

    @Getter
    private final AddProjectForm addProjectForm = new AddProjectForm();

    public HomePage() {
        super(ADD_PROJECT_BUTTON_LOCATOR, "Projects Page");
    }

    public void clickProjectBy(String name) {
        List<IButton> currentProjects = getCurrentProjects();

        for (IButton project : currentProjects) {
            if (project.getText().strip().equals(name)) {
                project.click();
                return;
            }
        }

        throw new IllegalArgumentException("Cannot find project with name: %s".formatted(name));
    }

    public List<String> getProjectNames() {
        return getCurrentProjects().stream()
                .map(IElement::getText)
                .toList();
    }

    public void clickAddProjectButton() {
        addProjectButton.click();
        addProjectForm.state().waitForDisplayed();
    }

    private List<IButton> getCurrentProjects() {
        return getElementFactory().findElements(PROJECTS_LOCATOR, IButton.class);
    }
}