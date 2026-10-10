package ru.kaelth4s.framework.util;

import net.datafaker.Faker;

public final class RandomUtil {
    private static final Faker faker = new Faker();

    private RandomUtil() {
    }

    public static String randomTitle() {
        return faker.lorem().sentence();
    }

    public static String randomBody() {
        return faker.lorem().paragraph();
    }

    public static long randomLong(long start, long end) {
        return faker.number().numberBetween(start, end);
    }
}