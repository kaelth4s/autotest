package ru.kaelth4s.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T mapFromResources(String fileName, Class<T> tClass) {
        try (InputStream is = tClass.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null)
                throw new IllegalArgumentException("Файл не найден в resources: " + fileName);

            return mapper.readValue(is, tClass);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось загрузить " + fileName + ": ", e);
        }
    }
}
