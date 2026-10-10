package ru.kaelth4s.framework.ui.config;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.UnableToSetCookieException;

import java.util.Set;

import static aquality.selenium.browser.AqualityServices.getBrowser;
import static aquality.selenium.browser.AqualityServices.getLogger;

public final class CookieManager {
    private static Set<Cookie> savedCookies;

    private CookieManager() {
    }

    public static void save(Set<Cookie> cookies) {
        savedCookies = cookies;
    }

    public static void load() {
        if (!isExists()) {
            throw new IllegalStateException("Cannot load cookies, because not found saved cookies!");
        }
        getBrowser().getDriver().manage().deleteAllCookies();
        for (Cookie cookie : savedCookies) {
            try {
                getBrowser().getDriver().manage().addCookie(cookie);
            } catch (UnableToSetCookieException cookieException) {
                getLogger().warn("Cookie '%s' rejected by browser".formatted(cookie.getName()));
            }
        }
    }

    private static boolean isExists() {
        return savedCookies != null;
    }
}