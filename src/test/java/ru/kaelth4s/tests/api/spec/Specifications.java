package ru.kaelth4s.tests.api.spec;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import ru.kaelth4s.tests.api.enums.ContentType;
import ru.kaelth4s.tests.api.enums.Header;

public final class Specifications {
    private static final String AUTHORIZATION_VALUE_TEMPLATE = "OAuth %s";

    private Specifications() {
    }

    public static RequestSpecBuilder builder(String baseUrl) {
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .addFilter(new AqualityLoggingFilter());
    }

    public static RequestSpecification disk(String baseUrl, String oauthToken) {
        return builder(baseUrl)
                .setContentType(ContentType.JSON.getValue())
                .setAccept(ContentType.JSON.getValue())
                .addHeader(Header.AUTHORIZATION.getValue(), AUTHORIZATION_VALUE_TEMPLATE.formatted(oauthToken))
                .build();
    }
}