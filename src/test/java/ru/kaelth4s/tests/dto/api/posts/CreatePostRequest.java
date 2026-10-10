package ru.kaelth4s.tests.dto.api.posts;

public record CreatePostRequest(
        int userId,
        String title,
        String body
) {
}