package ru.kaelth4s.tests.api.client;

import io.restassured.response.Response;
import ru.kaelth4s.framework.api.client.ApiClient;
import ru.kaelth4s.framework.api.client.ApiResponse;
import ru.kaelth4s.tests.api.dto.resources.move.PostResourcesMoveResponse;
import ru.kaelth4s.tests.api.enums.Endpoint;
import ru.kaelth4s.tests.api.enums.Query;

import java.util.Map;

import static ru.kaelth4s.tests.api.config.SpecificationsManager.getDiskSpec;
import static ru.kaelth4s.tests.util.DiskPathUtil.replaceExtension;

public class ResourcesMoveClient extends ApiClient {
    public ApiResponse<PostResourcesMoveResponse> moveFileOnDisk(String fromPath, String toPath) {
        Response response = postWithQuery(
                getDiskSpec(),
                Endpoint.RESOURCES_MOVE.getPath(),
                Map.of(
                        Query.FROM.getValue(), fromPath,
                        Query.PATH.getValue(), toPath
                )
        );
        return toApiResponse(response, PostResourcesMoveResponse.class);
    }

    public ApiResponse<PostResourcesMoveResponse> changeFileExtension(String diskPath, String newExtension) {
        String newPath = replaceExtension(diskPath, newExtension);
        return moveFileOnDisk(diskPath, newPath);
    }
}