package ru.kaelth4s.tests.enums;

public enum Endpoint {
    POSTS("/posts"),
    USERS("/users");

    private final String path;

    Endpoint(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String byId() {
        return path + "/{id}";
    }
}