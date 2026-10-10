package ru.kaelth4s.tests.api.dto.trash.resources;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetTrashResourcesResponse(
        @JsonProperty("_embedded") TrashResourcesEmbedded embedded,
        String name,
        String path,
        String type,
        long size
) {
}