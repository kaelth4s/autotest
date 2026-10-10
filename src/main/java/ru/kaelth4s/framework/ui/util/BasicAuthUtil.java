package ru.kaelth4s.framework.ui.util;

import org.openqa.selenium.UsernameAndPassword;
import org.openqa.selenium.bidi.BiDiException;
import org.openqa.selenium.bidi.module.Network;
import org.openqa.selenium.bidi.network.AddInterceptParameters;
import org.openqa.selenium.bidi.network.InterceptPhase;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import static aquality.selenium.browser.AqualityServices.getBrowser;
import static aquality.selenium.browser.AqualityServices.getLogger;

/**
 * Enables HTTP Basic Authentication for the site under test.
 * <p>
 * Chrome and Edge are handled by Aquality via Chrome DevTools Protocol,
 * Firefox has no DevTools protocol support, so WebDriver BiDi is used there
 * (the {@code webSocketUrl} capability must be enabled in the Firefox settings).
 */
public final class BasicAuthUtil {
    private static final String BIDI_DISABLED_MESSAGE =
            "BiDi is not enabled for Firefox. Set 'driverSettings.firefox.capabilities.webSocketUrl=true' in settings.firefox.json";

    private static Network biDiNetwork;

    private BasicAuthUtil() {
    }

    public static void apply(String host, String login, String password) {
        RemoteWebDriver driver = getBrowser().getDriver();

        if (driver instanceof FirefoxDriver firefoxDriver) {
            applyViaBiDi(firefoxDriver, login, password);
            return;
        }

        getLogger().info("Adding Basic Authentication handler via DevTools for host '%s'", host);
        getBrowser().network().addBasicAuthentication(host, login, password);
    }

    private static void applyViaBiDi(FirefoxDriver driver, String login, String password) {
        if (biDiNetwork != null) {
            return;
        }

        getLogger().info("Adding Basic Authentication handler via WebDriver BiDi");

        Network network;
        try {
            network = new Network(driver);
        } catch (BiDiException biDiException) {
            throw new IllegalStateException(BIDI_DISABLED_MESSAGE, biDiException);
        }

        network.addIntercept(new AddInterceptParameters(InterceptPhase.AUTH_REQUIRED));
        network.onAuthRequired(responseDetails -> network.continueWithAuth(
                responseDetails.getRequest().getRequestId(),
                new UsernameAndPassword(login, password)));

        biDiNetwork = network;
    }
}