package ru.kaelth4s.framework.dto;

public record WaitConfig(
        int timeout,
        int pollingMillis
) {}