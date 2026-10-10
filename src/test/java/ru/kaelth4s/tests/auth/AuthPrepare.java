package ru.kaelth4s.tests.auth;

import org.apache.logging.log4j.ThreadContext;
import org.openqa.selenium.Cookie;
import ru.kaelth4s.framework.config.CookieManager;
import ru.kaelth4s.tests.auth.config.DebugTokenManager;
import ru.kaelth4s.tests.auth.enums.AuthEnvironmentKey;
import ru.kaelth4s.tests.auth.page.DebugTokenPage;
import ru.kaelth4s.tests.ui.page.MainPage;
import ru.kaelth4s.tests.ui.page.PassportPage;

import java.util.Set;

import static aquality.selenium.browser.AqualityServices.getBrowser;
import static ru.kaelth4s.tests.auth.config.AuthEnvironmentDataManager.getAuthEnvKey;
import static ru.kaelth4s.tests.auth.config.AuthEnvironmentDataManager.getAuthEnvironmentData;
import static ru.kaelth4s.tests.ui.config.UiEnvironmentDataManager.getUiEnvironmentData;

public final class AuthPrepare {
    private AuthPrepare() {
    }

    public static void auth() {
        try {
            ThreadContext.put("authRun", "true");

            getBrowser().goTo(getAuthEnvironmentData().baseUrl());

            PassportPage passportPage = new PassportPage();
            passportPage.state().waitForDisplayed();
            passportPage.clickMoreButton();
            passportPage.clickToLoginAuthButton();
            passportPage.inputLogin(getAuthEnvKey(AuthEnvironmentKey.YANDEX_LOGIN));
            passportPage.clickToPasswordButton();
            passportPage.inputPassword(getAuthEnvKey(AuthEnvironmentKey.YANDEX_PASSWORD));
            passportPage.clickAuthorizeButton();
            if (passportPage.waitSFADisplayed()) {
                passportPage.clickSFAButton();
                if (!passportPage.waitUserPassSFA())
                    throw new IllegalStateException("You need to pass 2FA for test case");
            }
            if (passportPage.waitLaterButton())
                passportPage.clickLaterButton();
            if (passportPage.waitSkipButton())
                passportPage.clickSkipIdentificationButton();
            if (passportPage.waitAuthorizeAppDisplayed()) {
                passportPage.clickAuthorizeAppButton();
                passportPage.clickAuthorizeAppWithAccButton();
            }

            DebugTokenPage debugTokenPage = new DebugTokenPage();
            if (!debugTokenPage.state().waitForDisplayed())
                throw new IllegalStateException("Something went wrong on auth");

            String token = debugTokenPage.getDebugToken();
            DebugTokenManager.save(token);

            getBrowser().goTo(getUiEnvironmentData().baseUrl());

            MainPage mainPage = new MainPage();
            mainPage.state().waitForDisplayed();

            Set<Cookie> cookies = getBrowser().getDriver().manage().getCookies();
            CookieManager.save(cookies);
        } finally {
            ThreadContext.remove("authRun");
        }
    }
}