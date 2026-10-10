package ru.kaelth4s.api.dto.posts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PostResponse(
        int id,
        int userId,
        String title,
        String body
) {
}