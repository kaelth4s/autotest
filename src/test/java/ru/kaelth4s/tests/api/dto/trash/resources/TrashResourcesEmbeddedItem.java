package ru.kaelth4s.tests.api.dto.trash.resources;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TrashResourcesEmbeddedItem(
        String name,
        String path,
        String type,
        String deleted,
        long size,
        @JsonProperty("origin_path") String originPath
) {
}