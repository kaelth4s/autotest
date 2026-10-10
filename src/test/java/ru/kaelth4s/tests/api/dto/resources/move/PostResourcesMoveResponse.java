package ru.kaelth4s.tests.api.dto.resources.move;

public record PostResourcesMoveResponse(
        String href,
        String method,
        boolean templated
) {
}