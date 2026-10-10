package ru.kaelth4s.tests.util;

public final class DiskPathUtil {
    private static final char DOT = '.';
    private static final char SLASH = '/';

    private DiskPathUtil() {
    }

    public static String replaceExtension(String diskPath, String newExtension) {
        int lastSlash = diskPath.lastIndexOf(SLASH);
        int lastDot = diskPath.lastIndexOf(DOT);

        if (lastDot <= lastSlash + 1) {
            return diskPath + DOT + newExtension;
        }
        return diskPath.substring(0, lastDot + 1) + newExtension;
    }
}