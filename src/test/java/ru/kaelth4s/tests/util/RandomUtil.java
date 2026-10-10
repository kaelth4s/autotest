package ru.kaelth4s.tests.util;

import net.datafaker.Faker;
import ru.kaelth4s.framework.util.FileUtil;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class RandomUtil {
    private static final Faker faker = new Faker();
    private static final DateTimeFormatter FILE_NAME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS");
    private static final String TEST_FILES_DIRECTORY = "test-files/";
    private static final String RANDOM_TEXT_FILE_NAME_TEMPLATE = "random-%s.txt";

    private RandomUtil() {
    }

    public static Path writeRandomTextFile() {
        Path randomTextFilePath = Path.of(TEST_FILES_DIRECTORY + RANDOM_TEXT_FILE_NAME_TEMPLATE.formatted(LocalDateTime.now().format(FILE_NAME_FORMAT)));
        FileUtil.writeFile(randomTextFilePath, faker.lorem().paragraph());
        return randomTextFilePath;
    }
}