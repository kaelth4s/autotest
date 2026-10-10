package ru.kaelth4s.tests.dto.api.posts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PostResponse(
        int id,
        int userId,
        String title,
        String body
) {
}