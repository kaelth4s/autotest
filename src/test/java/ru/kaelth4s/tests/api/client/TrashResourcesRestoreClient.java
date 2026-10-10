package ru.kaelth4s.tests.api.client;

import io.restassured.response.Response;
import ru.kaelth4s.framework.api.client.ApiClient;
import ru.kaelth4s.framework.api.client.ApiResponse;
import ru.kaelth4s.tests.api.dto.trash.resources.restore.PutTrashResourcesRestoreResponse;
import ru.kaelth4s.tests.api.enums.Endpoint;
import ru.kaelth4s.tests.api.enums.Query;

import java.util.Map;

import static ru.kaelth4s.tests.api.config.SpecificationsManager.getDiskSpec;

public class TrashResourcesRestoreClient extends ApiClient {
    public ApiResponse<PutTrashResourcesRestoreResponse> restoreFileFromTrash(String trashPath) {
        Response response = putWithQuery(
                getDiskSpec(),
                Endpoint.TRASH_RESOURCES_RESTORE.getPath(),
                Map.of(Query.PATH.getValue(), trashPath)
        );
        return toApiResponse(response, PutTrashResourcesRestoreResponse.class);
    }
}