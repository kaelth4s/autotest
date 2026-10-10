package ru.kaelth4s.tests.db.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class BuildNumberUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    private BuildNumberUtil() {
    }

    public static long resolve() {
        return Long.parseLong(LocalDateTime.now().format(FORMATTER));
    }
}