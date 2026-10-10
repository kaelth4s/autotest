package ru.kaelth4s.tests.api.dto.trash.resources;

import java.util.List;

public record TrashResourcesEmbedded(
        List<TrashResourcesEmbeddedItem> items,
        int limit,
        int offset,
        int total,
        String path
) {
}