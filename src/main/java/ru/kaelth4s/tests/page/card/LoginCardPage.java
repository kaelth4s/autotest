package ru.kaelth4s.tests.page.card;

import aquality.selenium.core.elements.ElementState;
import aquality.selenium.elements.interfaces.IButton;
import aquality.selenium.elements.interfaces.ICheckBox;
import aquality.selenium.elements.interfaces.ILabel;
import aquality.selenium.elements.interfaces.ITextBox;
import org.openqa.selenium.By;
import ru.kaelth4s.framework.enums.Attribute;
import ru.kaelth4s.tests.page.CardPage;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.random.RandomGenerator;

public class LoginCardPage extends CardPage {
    private static final DateTimeFormatter TIMER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final String OPENED_STATE_CLASS_ATTRIBUTE = "open";
    private final ICheckBox termsAndConditionsCheckbox = getElementFactory()
            .getCheckBox(By.id("accept-terms-conditions"), "Terms And Conditions", ElementState.EXISTS_IN_ANY_STATE);
    private final ILabel termsAndConditionsCheckboxLabel = getElementFactory()
            .getLabel(By.xpath("//label[@for='accept-terms-conditions']"), "Terms And Conditions");
    private final ITextBox passwordInput = getElementFactory()
            .getTextBox(By.xpath("//input[contains(@placeholder,'Password')]"), "Password Input");
    private final ITextBox emailInput = getElementFactory()
            .getTextBox(By.xpath("//input[contains(@placeholder,'email')]"), "Email Input");
    private final ITextBox domainInput = getElementFactory()
            .getTextBox(By.xpath("//input[contains(@placeholder,'Domain')]"), "Domain Input");
    private final IButton nationalDomainDropdown = getElementFactory()
            .getButton(By.xpath("//*[contains(@class,'dropdown__header')]/parent::*"), "National Domain Dropdown");
    private final List<IButton> nationalDomains = getElementFactory()
            .findElements(By.xpath("//*[contains(@class, 'dropdown__list-item') and not(contains(text(), 'other'))]"), IButton.class, ElementState.EXISTS_IN_ANY_STATE);
    private final IButton nextButton = getElementFactory()
            .getButton(By.xpath("//*[contains(text(), 'Next')]"), "Next");

    private final ILabel timerLabel = getElementFactory()
            .getLabel(By.className("timer"), "Timer");

    public LoginCardPage() {
        super(By.className("login-form-with-pw-check"), "Login Card Page");
    }

    public void acceptTermsAndConditions() {
        if (termsAndConditionsCheckbox.isChecked())
            termsAndConditionsCheckboxLabel.click();
    }

    public void inputPassword(String value) {
        passwordInput.clearAndType(value);
    }

    public void inputEmail(String value) {
        emailInput.clearAndType(value);
    }

    public void inputDomain(String value) {
        domainInput.clearAndType(value);
    }

    public void selectRandomNationalDomain() {
        if (!nationalDomainDropdown.getAttribute(Attribute.CLASS.getName()).contains(OPENED_STATE_CLASS_ATTRIBUTE))
            nationalDomainDropdown.click();
        int randomIndex = RandomGenerator.getDefault().nextInt(nationalDomains.size());
        nationalDomains.get(randomIndex).getMouseActions().scrollToElement();
        nationalDomains.get(randomIndex).click();
    }

    public void clickNextButton() {
        nextButton.click();
    }

    public LocalTime getTimerTime() {
        String timerText = timerLabel.getText();
        return LocalTime.parse(timerText, TIMER);
    }
}