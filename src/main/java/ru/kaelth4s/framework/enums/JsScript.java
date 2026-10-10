package ru.kaelth4s.framework.enums;

public enum JsScript {
    UPLOAD_FILE("js/upload_file.js");

    private final String path;

    JsScript(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}