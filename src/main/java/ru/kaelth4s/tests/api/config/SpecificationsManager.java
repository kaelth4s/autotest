package ru.kaelth4s.tests.api.config;

import io.restassured.specification.RequestSpecification;
import ru.kaelth4s.tests.api.spec.Specifications;

import static ru.kaelth4s.tests.api.config.ApiEnvironmentDataManager.getApiEnvironmentData;

public final class SpecificationsManager {
    private static final RequestSpecification BASE_SPEC = loadBaseSpec();

    private SpecificationsManager() {
    }

    public static RequestSpecification getBaseSpec() {
        return BASE_SPEC;
    }

    private static RequestSpecification loadBaseSpec() {
        return Specifications.base(getApiEnvironmentData().baseUrl());
    }
}