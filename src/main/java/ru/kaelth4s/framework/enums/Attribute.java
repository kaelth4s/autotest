package ru.kaelth4s.framework.enums;

public enum Attribute {
    ID("id"),
    CLASS("class");

    private final String name;

    Attribute(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}