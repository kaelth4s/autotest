package ru.kaelth4s.framework.client;

public record ApiResponse<T>(
        int statusCode,
        String contentType,
        T body
) {
}
