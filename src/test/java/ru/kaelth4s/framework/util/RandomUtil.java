package ru.kaelth4s.framework.util;

import net.datafaker.Faker;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public final class RandomUtil {
    private static final String CYRILLIC = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
    private static final int PASSWORD_BASE_MINIMUM_LENGTH = 8;
    private static final int PASSWORD_BASE_MAXIMUM_LENGTH = 20;
    private static final Random RANDOM = new Random();
    private static final Faker faker = new Faker();

    private RandomUtil() {
    }

    public static String getRandomEmailName() {
        return faker.name().firstName().toLowerCase() + faker.number().digits(3);
    }

    public static String getRandomEmailDomain() {
        return faker.lorem().word();
    }

    public static String getRandomPasswordForEmail(String email) {
        char emailLetter = email.chars()
                .mapToObj(c -> (char) c)
                .filter(Character::isLetter)
                .findAny()
                .orElseThrow();

        String base = faker.lorem().characters(PASSWORD_BASE_MINIMUM_LENGTH, PASSWORD_BASE_MAXIMUM_LENGTH, true, true)
                + getRandomCyrillicChar()
                + emailLetter;

        List<Character> chars = base.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(chars);

        return chars.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    public static char getRandomCyrillicChar() {
        return CYRILLIC.charAt(RANDOM.nextInt(CYRILLIC.length()));
    }

    public static <T> T getRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("Cannot pick a random element from an empty or null list");
        }
        return list.get(RANDOM.nextInt(list.size()));
    }
}