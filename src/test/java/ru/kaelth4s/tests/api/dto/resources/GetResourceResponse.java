package ru.kaelth4s.tests.api.dto.resources;

public record GetResourceResponse(
        String name,
        String path,
        String type,
        String md5,
        Long size
) {
}