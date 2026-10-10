package ru.kaelth4s.api.client;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import ru.kaelth4s.api.dto.users.UserResponse;
import ru.kaelth4s.api.enums.Endpoint;
import ru.kaelth4s.framework.client.ApiClient;
import ru.kaelth4s.framework.client.ApiResponse;

import java.util.List;
import java.util.Map;

import static ru.kaelth4s.api.config.SpecificationsManager.getBaseSpec;

public class UserClient extends ApiClient {
    public ApiResponse<List<UserResponse>> getAll() {
        Response response = get(getBaseSpec(), Endpoint.USERS.getPath());
        return new ApiResponse<>(
                response.statusCode(),
                response.contentType(),
                response.as(new TypeRef<>() {
                })
        );
    }

    public ApiResponse<UserResponse> getById(int id) {
        Response response = get(getBaseSpec(), Endpoint.USERS.byId(), Map.of("id", id));
        return new ApiResponse<>(
                response.statusCode(),
                response.contentType(),
                response.as(UserResponse.class));
    }
}
