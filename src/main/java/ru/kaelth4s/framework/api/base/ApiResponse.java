package ru.kaelth4s.framework.api.base;

public record ApiResponse<T>(
        int statusCode,
        String contentType,
        T body
) {
}