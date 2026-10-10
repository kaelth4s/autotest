package ru.kaelth4s.framework.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class FileUtil {
    private FileUtil() {
    }

    public static String readResource(String resourcePath) {
        try (InputStream is = FileUtil.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalStateException("Script not found in classpath: %s".formatted(resourcePath));
            }
            return new String(Objects.requireNonNull(is).readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException readException) {
            throw new UncheckedIOException("Failed to read resource: %s".formatted(resourcePath), readException);
        }
    }

    public static String readFile(Path filePath) {
        try {
            return Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (IOException readException) {
            throw new UncheckedIOException("Failed to read file: %s".formatted(filePath), readException);
        }
    }
}