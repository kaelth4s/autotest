package ru.kaelth4s.tests.api.client;

import io.restassured.response.Response;
import ru.kaelth4s.framework.api.client.ApiClient;
import ru.kaelth4s.framework.api.client.ApiResponse;
import ru.kaelth4s.tests.api.dto.resources.GetResourceResponse;
import ru.kaelth4s.tests.api.enums.Endpoint;
import ru.kaelth4s.tests.api.enums.Query;

import java.util.Map;

import static ru.kaelth4s.tests.api.config.SpecificationsManager.getDiskSpec;

public class ResourceClient extends ApiClient {
    public ApiResponse<Void> deleteFilePermanently(String diskFilePath) {
        Response response = deleteWithQuery(
                getDiskSpec(),
                Endpoint.RESOURCES.getPath(),
                Map.of(
                        Query.PATH.getValue(), diskFilePath,
                        Query.PERMANENTLY.getValue(), true
                )
        );
        return new ApiResponse<>(
                response.statusCode(),
                response.contentType(),
                null
        );
    }

    public ApiResponse<GetResourceResponse> getMetadata(String diskPath) {
        Response response = getWithQuery(
                getDiskSpec(),
                Endpoint.RESOURCES.getPath(),
                Map.of(Query.PATH.getValue(), diskPath)
        );
        return toApiResponse(response, GetResourceResponse.class);
    }
}