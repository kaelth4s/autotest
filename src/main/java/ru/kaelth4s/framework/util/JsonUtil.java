package ru.kaelth4s.framework.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static aquality.selenium.browser.AqualityServices.getLogger;

public final class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    private JsonUtil() {
    }

    public static <T> T mapFromResourcesJson(String fileName, Class<T> tClass) {
        getLogger().info("Reading the class '%s' from a resource file named '%s'", tClass.getName(), fileName);

        try (InputStream is = tClass.getResourceAsStream("/%s".formatted(fileName))) {
            return mapper.readValue(is, tClass);
        } catch (IOException readException) {
            throw new UncheckedIOException("Could not read JSON from a file in resources", readException);
        }
    }

    public static <T> T mapFromJson(Path filePath, TypeReference<T> typeRef) {
        getLogger().info("Reading '%s' from '%s'", typeRef.getType(), filePath);

        try {
            return mapper.readValue(FileUtil.readFile(filePath), typeRef);
        } catch (JsonProcessingException jsonException) {
            throw new UncheckedIOException("Could not parse JSON file: %s".formatted(filePath), jsonException);
        }
    }

    public static void writeJson(Path filePath, Object value) {
        getLogger().info("Writing JSON to '%s'", filePath);
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), value);
        } catch (IOException writeException) {
            throw new UncheckedIOException("Could not write JSON file: %s".formatted(filePath), writeException);
        }
    }
}