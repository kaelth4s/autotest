package ru.kaelth4s.tests.ui.form;

import aquality.selenium.elements.interfaces.IButton;
import aquality.selenium.elements.interfaces.ITextBox;
import aquality.selenium.forms.Form;
import org.openqa.selenium.By;
import ru.kaelth4s.framework.ui.util.FrameUtil;
import ru.kaelth4s.framework.ui.util.JsUtil;
import ru.kaelth4s.tests.ui.enums.TestJs;

import java.util.function.Supplier;

import static aquality.selenium.browser.AqualityServices.getBrowser;

public class AddProjectForm extends Form {
    private static final String FRAME_ID = "addProjectFrame";
    private static final String POPUP_SELECTOR = ".modal";

    private final ITextBox projectNameInput = getElementFactory()
            .getTextBox(By.id("projectName"), "Project Name");
    private final IButton saveButton = getElementFactory()
            .getButton(By.xpath("//*[contains(@type,'submit')]"), "Submit");
    private final IButton successAlert = getElementFactory()
            .getButton(By.className("alert-success"), "Success Alert");

    public AddProjectForm() {
        super(By.className("modal-dialog"), "Add Project");
    }

    public void enterProjectName(String value) {
        inFrame(() -> {
            projectNameInput.state().waitForDisplayed();
            projectNameInput.getElement().clear();
            projectNameInput.getElement().sendKeys(value);
        });
    }

    public void clickSave() {
        inFrame(saveButton::click);
    }

    public boolean waitForSuccessAlert() {
        return inFrame(() -> successAlert.state().waitForDisplayed());
    }

    public boolean closePopUp() {
        JsUtil.execute(TestJs.CLOSE_POP_UP, POPUP_SELECTOR);
        return state().waitForNotDisplayed();
    }

    private void inFrame(Runnable action) {
        boolean hasFrame = !getBrowser().getDriver()
                .findElements(By.id(FRAME_ID))
                .isEmpty();
        if (!hasFrame) {
            action.run();
            return;
        }
        FrameUtil.switchToFrame(FRAME_ID);
        try {
            action.run();
        } finally {
            FrameUtil.switchToPage();
        }
    }

    private <T> T inFrame(Supplier<T> action) {
        boolean hasFrame = !getBrowser().getDriver()
                .findElements(By.id(FRAME_ID))
                .isEmpty();
        if (!hasFrame) {
            return action.get();
        }
        FrameUtil.switchToFrame(FRAME_ID);
        try {
            return action.get();
        } finally {
            FrameUtil.switchToPage();
        }
    }
}