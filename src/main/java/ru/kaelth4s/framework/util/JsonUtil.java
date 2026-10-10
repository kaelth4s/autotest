package ru.kaelth4s.framework.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    private JsonUtil() {}

    public static <T> T mapFromResourcesJson(String fileName, Class<T> tClass) {
        log.info("Reading the class '{}' from a resource file named '{}'", tClass, fileName);
        try (InputStream is = tClass.getResourceAsStream("/" + fileName)) {
            return mapper.readValue(is, tClass);
        } catch (IOException e) {
            throw new RuntimeException("Could not read JSON from a file in resources", e);
        }
    }
}