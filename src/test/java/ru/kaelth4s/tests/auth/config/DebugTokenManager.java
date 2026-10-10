package ru.kaelth4s.tests.auth.config;

import ru.kaelth4s.framework.util.FileUtil;

import java.nio.file.Path;

public final class DebugTokenManager {
    private static final Path TOKEN_PATH = Path.of("auth-session/access_token.txt");

    private DebugTokenManager() {
    }

    public static void save(String token) {
        FileUtil.writeFile(TOKEN_PATH, token);
    }

    public static String load() {
        return FileUtil.readFile(TOKEN_PATH);
    }
}