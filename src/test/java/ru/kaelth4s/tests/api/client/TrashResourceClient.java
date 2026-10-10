package ru.kaelth4s.tests.api.client;

import io.restassured.response.Response;
import ru.kaelth4s.framework.api.client.ApiClient;
import ru.kaelth4s.framework.api.client.ApiResponse;
import ru.kaelth4s.tests.api.dto.trash.resources.GetTrashResourcesResponse;
import ru.kaelth4s.tests.api.enums.Endpoint;
import ru.kaelth4s.tests.api.enums.Query;
import ru.kaelth4s.tests.api.enums.SortAttribute;

import java.util.Map;

import static ru.kaelth4s.tests.api.config.SpecificationsManager.getDiskSpec;

public class TrashResourceClient extends ApiClient {
    public ApiResponse<GetTrashResourcesResponse> getAllTrashSorted() {
        Response response = getWithQuery(
                getDiskSpec(),
                Endpoint.TRASH_RESOURCES.getPath(),
                Map.of(Query.SORT.getValue(), SortAttribute.DELETED_DESC.getValue())
        );
        return toApiResponse(response, GetTrashResourcesResponse.class);
    }
}