package ru.kaelth4s.framework.config;

import com.fasterxml.jackson.core.type.TypeReference;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.UnableToSetCookieException;
import ru.kaelth4s.framework.util.JsonUtil;
import ru.kaelth4s.tests.auth.dto.CookieData;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static aquality.selenium.browser.AqualityServices.getBrowser;
import static aquality.selenium.browser.AqualityServices.getLogger;

public final class CookieManager {
    private static final Path COOKIES_PATH = Path.of("auth-session/cookies.json");

    private CookieManager() {
    }

    public static void save(Set<Cookie> cookies) {
        List<CookieData> data = cookies.stream()
                .map(CookieData::from)
                .collect(Collectors.toList());
        JsonUtil.writeJson(COOKIES_PATH, data);
    }

    public static void load() {
        if (!isExists()) {
            throw new IllegalStateException("Cannot load cookies, because the .json file is empty!");
        }
        List<CookieData> data = JsonUtil.mapFromJson(COOKIES_PATH, new TypeReference<>() {
        });
        Set<Cookie> cookies = data.stream()
                .map(CookieData::toCookie)
                .collect(Collectors.toSet());
        getBrowser().getDriver().manage().deleteAllCookies();
        for (Cookie cookie : cookies) {
            try {
                getBrowser().getDriver().manage().addCookie(cookie);
            } catch (UnableToSetCookieException cookieException) {
                getLogger().warn("Cookie '%s' rejected by browser".formatted(cookie.getName()));
            }
        }
    }

    public static boolean isExists() {
        return Files.exists(COOKIES_PATH);
    }
}