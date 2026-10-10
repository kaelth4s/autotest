package ru.kaelth4s.tests.api.dto.resources.upload;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetResourcesUploadResponse(
        String href,
        String method,
        boolean templated,
        @JsonProperty("operation_id") String operationId
) {
}