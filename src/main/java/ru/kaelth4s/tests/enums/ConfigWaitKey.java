package ru.kaelth4s.tests.enums;

public enum ConfigWaitKey {
    PROGRESS_BAR("progressBar");

    private final String key;

    ConfigWaitKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}