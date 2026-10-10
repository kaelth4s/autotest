package ru.kaelth4s.tests.ui.page;

import aquality.selenium.elements.interfaces.IButton;
import aquality.selenium.elements.interfaces.ITextBox;
import aquality.selenium.forms.Form;
import org.openqa.selenium.By;

import java.time.Duration;

import static aquality.selenium.browser.AqualityServices.getConditionalWait;
import static ru.kaelth4s.tests.auth.config.AuthConfigManager.getAuthEnvironmentData;

public class PassportPage extends Form {
    private final IButton moreButton = getElementFactory()
            .getButton(By.xpath("//*[contains(@data-testid,'split-add-user-more-button')]"), "More");
    private final IButton toLoginAuthButton = getElementFactory()
            .getButton(By.xpath("//*[contains(@data-testid,'menu-option-switchToLogin')]"), "Login Auth");
    private final ITextBox loginAuthInput = getElementFactory()
            .getTextBox(By.xpath("//*[contains(@autocomplete,'username')]"), "Login Auth Input");
    private final IButton toPasswordButton = getElementFactory()
            .getButton(By.xpath("//*[contains(@data-testid,'split-add-user-next-login')]"), "To Password");
    private final ITextBox passwordAuthInput = getElementFactory()
            .getTextBox(By.xpath("//*[contains(@autocomplete,'current-password')]"), "Password Auth Input");
    private final IButton authorizeButton = getElementFactory()
            .getButton(By.xpath("//*[contains(@data-testid,'password-next')]"), "Authorize");
    private final IButton laterButton = getElementFactory()
            .getButton(By.xpath("//*[contains(@data-testid,'webauthn-reg-later-button')]"), "Later");
    private final IButton skipIdentificationButton = getElementFactory()
            .getButton(By.xpath("//*[contains(@data-testid,'identification-promo-start-skip-btn')]"), "Skip Identification");
    private final IButton SFASubmitButton = getElementFactory()
            .getButton(By.xpath("//*[contains(@data-testid,'challenges-phone-confirmation-next')]"), "SFA Submit");
    private final ITextBox SFACodeInput = getElementFactory()
            .getTextBox(By.xpath("//*[contains(@data-testid,'code-input')]"), "SFA Code Input");
    private final IButton authorizeAppButton = getElementFactory()
            .getButton(By.className("AuthorizeApp-button"), "Authorize App");
    private final IButton authorizeAppWithAccButton = getElementFactory()
            .getButton(By.className("AuthorizeApp-buttons"), "Authorize App With Account");


    public PassportPage() {
        super(By.className("auth-layout"), "Passport Page");
    }

    public void clickMoreButton() {
        moreButton.click();
    }

    public void clickToLoginAuthButton() {
        toLoginAuthButton.click();
    }

    public void inputLogin(String value) {
        loginAuthInput.clearAndType(value);
    }

    public void clickToPasswordButton() {
        toPasswordButton.click();
    }

    public void inputPassword(String value) {
        passwordAuthInput.clearAndType(value);
    }

    public void clickAuthorizeButton() {
        authorizeButton.click();
    }

    public void clickLaterButton() {
        laterButton.click();
    }

    public void clickSkipIdentificationButton() {
        skipIdentificationButton.click();
    }

    public boolean waitLaterButton() {
        return getConditionalWait().waitFor(
                () -> laterButton.state().isDisplayed(),
                Duration.ofSeconds(getAuthEnvironmentData().timeoutAuthFormLoad())
        );
    }

    public boolean waitSkipButton() {
        return getConditionalWait().waitFor(
                () -> skipIdentificationButton.state().isDisplayed(),
                Duration.ofSeconds(getAuthEnvironmentData().timeoutAuthFormLoad())
        );
    }

    public boolean waitSFADisplayed() {
        return getConditionalWait().waitFor(
                () -> SFASubmitButton.state().isDisplayed(),
                Duration.ofSeconds(getAuthEnvironmentData().timeoutAuthFormLoad())
        );
    }

    public void clickSFAButton() {
        SFASubmitButton.click();
    }

    public boolean waitUserPassSFA() {
        return getConditionalWait().waitFor(
                () -> !SFACodeInput.state().isDisplayed(),
                Duration.ofSeconds(getAuthEnvironmentData().timeoutSFA())
        );
    }

    public boolean waitAuthorizeAppDisplayed() {
        return getConditionalWait().waitFor(
                () -> authorizeAppButton.state().isDisplayed(),
                Duration.ofSeconds(getAuthEnvironmentData().timeoutAuthFormLoad())
        );
    }

    public void clickAuthorizeAppButton() {
        authorizeAppButton.click();
    }

    public void clickAuthorizeAppWithAccButton() {
        authorizeAppWithAccButton.click();
    }
}
