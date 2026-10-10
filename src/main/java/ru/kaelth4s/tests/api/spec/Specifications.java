package ru.kaelth4s.tests.api.spec;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public final class Specifications {
    private Specifications() {
    }

    public static RequestSpecBuilder builder(String baseUrl) {
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .addFilter(new AqualityLoggingFilter());
    }

    public static RequestSpecification base(String baseUrl) {
        return builder(baseUrl)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }
}