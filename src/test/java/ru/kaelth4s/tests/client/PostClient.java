package ru.kaelth4s.tests.client;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import ru.kaelth4s.framework.client.ApiClient;
import ru.kaelth4s.framework.client.ApiResponse;
import ru.kaelth4s.tests.dto.api.posts.CreatePostRequest;
import ru.kaelth4s.tests.dto.api.posts.PostResponse;
import ru.kaelth4s.tests.enums.Endpoint;

import java.util.List;
import java.util.Map;

import static ru.kaelth4s.tests.config.SpecificationsManager.getBaseSpec;

public class PostClient extends ApiClient {
    public ApiResponse<List<PostResponse>> getAll() {
        Response response = get(getBaseSpec(), Endpoint.POSTS.getPath());
        return toApiResponse(response, new TypeRef<>() {
        });
    }

    public ApiResponse<PostResponse> getById(int id) {
        Response response = get(getBaseSpec(), Endpoint.POSTS.byId(), Map.of("id", id));
        return toApiResponse(response, PostResponse.class);
    }

    public ApiResponse<PostResponse> create(CreatePostRequest request) {
        Response response = post(getBaseSpec(), Endpoint.POSTS.getPath(), request);
        return toApiResponse(response, PostResponse.class);
    }

    public ApiResponse<Map<String, Object>> getRawById(int id) {
        Response response = get(getBaseSpec(), Endpoint.POSTS.byId(), Map.of("id", id));
        return new ApiResponse<>(
                response.statusCode(),
                response.contentType(),
                response.jsonPath().getMap("$"));
    }

    private <T> ApiResponse<T> toApiResponse(Response response, Class<T> type) {
        return new ApiResponse<>(
                response.statusCode(),
                response.contentType(),
                response.as(type));
    }

    private <T> ApiResponse<T> toApiResponse(Response response, TypeRef<T> type) {
        return new ApiResponse<>(
                response.statusCode(),
                response.contentType(),
                response.as(type));
    }
}
