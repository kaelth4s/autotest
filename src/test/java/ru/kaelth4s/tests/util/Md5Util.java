package ru.kaelth4s.tests.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class Md5Util {
    private static final String ALGORITHM_NAME = "MD5";

    private Md5Util() {
    }

    public static String ofBytes(byte[] content) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM_NAME);
            return HexFormat.of().formatHex(digest.digest(content));
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw new IllegalStateException("MD5 algorithm is not available", noSuchAlgorithmException);
        }
    }
}