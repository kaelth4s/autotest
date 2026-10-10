package ru.kaelth4s.tests.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.openqa.selenium.Cookie;

import java.util.Date;

public record CookieData(
        @JsonProperty("name") String name,
        @JsonProperty("value") String value,
        @JsonProperty("domain") String domain,
        @JsonProperty("path") String path,
        @JsonProperty("expiry") Long expiry,
        @JsonProperty("secure") boolean secure,
        @JsonProperty("httpOnly") boolean httpOnly,
        @JsonProperty("sameSite") String sameSite
) {
    public static CookieData from(Cookie cookie) {
        return new CookieData(
                cookie.getName(),
                cookie.getValue(),
                cookie.getDomain(),
                cookie.getPath(),
                cookie.getExpiry() != null ? cookie.getExpiry().getTime() : null,
                cookie.isSecure(),
                cookie.isHttpOnly(),
                cookie.getSameSite()
        );
    }

    public Cookie toCookie() {
        Date expiryDate = expiry != null ? new Date(expiry) : null;
        return new Cookie(name, value, domain, path, expiryDate, secure, httpOnly, sameSite);
    }
}