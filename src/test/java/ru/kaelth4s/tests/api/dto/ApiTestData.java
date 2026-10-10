package ru.kaelth4s.tests.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.kaelth4s.tests.api.dto.token.get.PostTokenGetRequest;

public record ApiTestData(
        PostTokenGetRequest postTokenGetRequest
) {
    @JsonCreator
    public static ApiTestData from(
            @JsonProperty("postTokenGetRequest") PostTokenGetRequest postTokenGetRequest
    ) {
        return new ApiTestData(
                postTokenGetRequest
        );
    }
}