package ru.kaelth4s.api.dto.posts;

public record CreatePostRequest(
        int userId,
        String title,
        String body
) {
}