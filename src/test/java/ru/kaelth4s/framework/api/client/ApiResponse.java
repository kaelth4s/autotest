package ru.kaelth4s.framework.api.client;

public record ApiResponse<T>(
        int statusCode,
        String contentType,
        T body
) {
}
