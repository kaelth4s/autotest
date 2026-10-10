package ru.kaelth4s.framework.util;

import net.datafaker.Faker;

public class RandomUtil {
    private RandomUtil() {}

    private static final Faker faker = new Faker();

    public static int getRandomPercent() {
        return (int) (Math.random() * 100);
    }

    public static String getRandomString() {
        return faker.lorem().characters(2, 25);
    }
}