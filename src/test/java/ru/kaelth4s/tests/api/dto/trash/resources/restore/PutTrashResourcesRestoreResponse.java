package ru.kaelth4s.tests.api.dto.trash.resources.restore;

public record PutTrashResourcesRestoreResponse(
        String href,
        String method,
        boolean templated
) {
}