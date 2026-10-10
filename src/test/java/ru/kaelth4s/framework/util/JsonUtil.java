package ru.kaelth4s.framework.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import static aquality.selenium.browser.AqualityServices.getLogger;

public final class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    private JsonUtil() {
    }

    public static <T> T mapFromResourcesJson(String fileName, Class<T> tClass) {
        getLogger().info("Reading the class '{}' from a resource file named '{}'", tClass, fileName);
        try (InputStream is = tClass.getResourceAsStream("/" + fileName)) {
            return mapper.readValue(is, tClass);
        } catch (IOException readError) {
            throw new UncheckedIOException("Could not read JSON from a file in resources", readError);
        }
    }
}