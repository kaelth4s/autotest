package ru.kaelth4s.framework.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class FileUtil {
    private FileUtil() {
    }

    public static String readResource(String resourcePath) {
        try (InputStream is = FileUtil.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalStateException(String.format("Script not found in classpath: %s", resourcePath));
            }
            return new String(Objects.requireNonNull(is).readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException readError) {
            throw new UncheckedIOException(String.format("Failed to read resource: %s", resourcePath), readError);
        }
    }
}