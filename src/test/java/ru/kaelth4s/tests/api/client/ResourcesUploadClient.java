package ru.kaelth4s.tests.api.client;

import io.restassured.response.Response;
import ru.kaelth4s.framework.api.client.ApiClient;
import ru.kaelth4s.framework.api.client.ApiResponse;
import ru.kaelth4s.framework.util.FileUtil;
import ru.kaelth4s.tests.api.dto.resources.upload.GetResourcesUploadResponse;
import ru.kaelth4s.tests.api.enums.Endpoint;
import ru.kaelth4s.tests.api.enums.Query;

import java.nio.file.Path;
import java.util.Map;

import static ru.kaelth4s.tests.api.config.SpecificationsManager.getDiskSpec;

public class ResourcesUploadClient extends ApiClient {
    public ApiResponse<Void> uploadFile(Path filePath, ApiResponse<GetResourcesUploadResponse> uploadLinkResponse) {
        return upload(FileUtil.readFileBytes(filePath), uploadLinkResponse);
    }

    public ApiResponse<Void> uploadResourceFile(String resourcePath, ApiResponse<GetResourcesUploadResponse> uploadLinkResponse) {
        return upload(FileUtil.readResourceBytes(resourcePath), uploadLinkResponse);
    }

    public ApiResponse<GetResourcesUploadResponse> getUploadFileLink(String diskPath) {
        Response response = getWithQuery(
                getDiskSpec(),
                Endpoint.RESOURCES_UPLOAD.getPath(),
                Map.of(
                        Query.PATH.getValue(), diskPath,
                        Query.OVERWRITE.getValue(), true
                )
        );
        return toApiResponse(response, GetResourcesUploadResponse.class);
    }

    private ApiResponse<Void> upload(byte[] content, ApiResponse<GetResourcesUploadResponse> uploadLinkResponse) {
        Response response = putBinary(uploadLinkResponse.body().href(), content);

        return new ApiResponse<>(
                response.statusCode(),
                response.contentType(),
                null
        );
    }
}