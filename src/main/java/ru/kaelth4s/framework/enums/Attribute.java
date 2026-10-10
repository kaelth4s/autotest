package ru.kaelth4s.framework.enums;

public enum Attribute {
    VALUE("value"),
    HREF("href"),
    CLASS("class");

    private final String name;
    Attribute(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}