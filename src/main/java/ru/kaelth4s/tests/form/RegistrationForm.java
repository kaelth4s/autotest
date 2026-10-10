package ru.kaelth4s.tests.form;

import org.openqa.selenium.By;
import ru.kaelth4s.framework.base.BaseForm;
import ru.kaelth4s.framework.element.Button;
import ru.kaelth4s.framework.element.Input;
import ru.kaelth4s.tests.dto.RegistrationData;

import static ru.kaelth4s.framework.util.WaitUtil.waitNotPresent;

public class RegistrationForm extends BaseForm {
    private final static By uniqueElementLocator = By.xpath("//*[@role='dialog']");

    private final Input firstNameInput = new Input(By.id("firstName"), "First Name Input");
    private final Input lastNameInput = new Input(By.id("lastName"), "Last Name Input");
    private final Input emailInput = new Input(By.id("userEmail"), "Email Input");
    private final Input ageInput = new Input(By.id("age"), "Age Name Input");
    private final Input salaryInput = new Input(By.id("salary"), "Salary Input");
    private final Input departmentInput = new Input(By.id("department"), "Department Input");

    private final Button submitButton = new Button(By.id("submit"), "Registration Form Submit Button");

    public RegistrationForm() {
        super(uniqueElementLocator, "Registration Form");
    }

    public void clickSubmitButton() {
        submitButton.click();
        waitNotPresent(uniqueElementLocator);
    }

    public void enterRegistrationData(RegistrationData value) {
        if (!isDisplayed()) throw new IllegalStateException("The registration form has not opened");
        firstNameInput.type(value.getFirstName());
        lastNameInput.type(value.getLastName());
        emailInput.type(value.getEmail());
        ageInput.type(String.valueOf(value.getAge()));
        salaryInput.type(String.valueOf(value.getSalary()));
        departmentInput.type(value.getDepartment());
    }
}