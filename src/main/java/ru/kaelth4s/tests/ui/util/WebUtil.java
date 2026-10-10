package ru.kaelth4s.tests.ui.util;

import java.util.Base64;

public final class WebUtil {

    private static final String DATA_URI_PREFIX = "data:";
    private static final char DATA_URI_SEPARATOR = ',';

    private WebUtil() {
    }

    public static byte[] decodeDataUri(String dataUri) {
        if (dataUri == null || !dataUri.startsWith(DATA_URI_PREFIX)) {
            throw new IllegalArgumentException("Not a data URI: %s".formatted(dataUri));
        }
        int separatorIndex = dataUri.indexOf(DATA_URI_SEPARATOR);
        if (separatorIndex < 0) {
            throw new IllegalArgumentException("Invalid data URI: no content separator");
        }
        return Base64.getDecoder().decode(dataUri.substring(separatorIndex + 1));
    }
}