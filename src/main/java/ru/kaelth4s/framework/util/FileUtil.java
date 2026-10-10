package ru.kaelth4s.framework.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtil {
    private FileUtil() {
    }

    public static String readResource(String resourcePath) {
        return new String(readResourceBytes(resourcePath), StandardCharsets.UTF_8);
    }

    public static void writeFile(Path filePath, String content) {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(filePath, content, StandardCharsets.UTF_8);
        } catch (IOException writeException) {
            throw new UncheckedIOException("Failed to write file: %s".formatted(filePath), writeException);
        }
    }

    public static void writeResourceFile(String resourcePath, String content) {
        try {
            URL resourceUrl = FileUtil.class.getClassLoader().getResource(resourcePath);
            if (resourceUrl == null) {
                throw new IllegalStateException("Resource not found on classpath: %s. Make sure it exists in src/.../resources.".formatted(resourcePath));
            }

            Path target = Path.of(resourceUrl.toURI());
            if (!Files.isRegularFile(target)) {
                throw new IllegalStateException("Resource is not a writable file (probably inside a JAR): %s".formatted(resourcePath));
            }

            Files.writeString(target, content, StandardCharsets.UTF_8);
        } catch (IOException | URISyntaxException writeException) {
            throw new UncheckedIOException(new IOException("Failed to write resource: %s".formatted(resourcePath), writeException));
        }
    }

    public static String readFile(Path filePath) {
        try {
            return Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (IOException readException) {
            throw new UncheckedIOException("Failed to read file: %s".formatted(filePath), readException);
        }
    }

    public static byte[] readFileBytes(Path filePath) {
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            return inputStream.readAllBytes();
        } catch (IOException readException) {
            throw new UncheckedIOException("Failed to read file: %s".formatted(filePath), readException);
        }
    }

    public static byte[] readResourceBytes(String resourcePath) {
        try (InputStream inputStream = FileUtil.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalStateException("Resource not found in classpath: %s".formatted(resourcePath));
            }
            return inputStream.readAllBytes();
        } catch (IOException readException) {
            throw new UncheckedIOException("Failed to read resource: %s".formatted(resourcePath), readException);
        }
    }
}