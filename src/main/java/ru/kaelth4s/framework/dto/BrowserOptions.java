package ru.kaelth4s.framework.dto;

public record BrowserOptions(
        String interfaceLanguage,
        String acceptLanguage,
        boolean startMaximized,
        boolean useIncognito,
        String[] additionalArguments
) {}