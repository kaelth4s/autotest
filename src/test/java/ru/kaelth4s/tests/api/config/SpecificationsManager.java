package ru.kaelth4s.tests.api.config;

import io.restassured.specification.RequestSpecification;
import ru.kaelth4s.tests.api.spec.Specifications;
import ru.kaelth4s.tests.auth.config.DebugTokenManager;

import static ru.kaelth4s.tests.api.config.ApiEnvironmentDataManager.getApiEnvironmentData;

public final class SpecificationsManager {
    private static final RequestSpecification DISK_SPEC = loadDiskSpec();

    private SpecificationsManager() {
    }

    public static RequestSpecification getDiskSpec() {
        return DISK_SPEC;
    }

    private static RequestSpecification loadDiskSpec() {
        return Specifications.disk(getApiEnvironmentData().baseUrl(), DebugTokenManager.load());
    }
}