package ru.kaelth4s.tests.api.client;

import io.restassured.response.Response;
import ru.kaelth4s.framework.api.base.ApiResponse;
import ru.kaelth4s.framework.api.client.ApiClient;
import ru.kaelth4s.tests.api.dto.token.get.PostTokenGetRequest;
import ru.kaelth4s.tests.api.dto.token.get.PostTokenGetResponse;
import ru.kaelth4s.tests.api.enums.Endpoint;

import java.util.Map;

import static ru.kaelth4s.tests.api.config.SpecificationsManager.getBaseSpec;

public class TokenGetClient extends ApiClient {
    private static final String VARIANT_QUERY_PARAM = "variant";

    public ApiResponse<PostTokenGetResponse> getToken(PostTokenGetRequest postTokenGetRequest) {
        Response response = postWithQuery(getBaseSpec(), Endpoint.TOKEN_GET.getPath(), Map.of(VARIANT_QUERY_PARAM, postTokenGetRequest.variant()));
        return new ApiResponse<>(
                response.statusCode(),
                response.contentType(),
                new PostTokenGetResponse(response.body().asString())
        );
    }
}