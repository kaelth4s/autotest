package ru.kaelth4s.api.config;

import io.restassured.specification.RequestSpecification;
import ru.kaelth4s.framework.spec.Specifications;

import static ru.kaelth4s.api.config.ApiEnvironmentDataManager.getApiEnvironmentData;

public final class SpecificationsManager {
    private static final RequestSpecification BASE_SPEC = load();

    private SpecificationsManager() {
    }

    public static RequestSpecification getBaseSpec() {
        return BASE_SPEC;
    }

    private static RequestSpecification load() {
        return Specifications.base(getApiEnvironmentData().baseUrl());
    }
}