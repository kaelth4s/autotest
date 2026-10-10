package ru.kaelth4s.framework.util;

public class ParserUtil {
    private ParserUtil() {}

    public static int parseInt(String str) {
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}