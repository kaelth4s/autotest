package ru.kaelth4s.tests.enums;

public enum Category {
    BROWSER_WINDOWS("Alerts, Frame & Windows", "/browser-windows"),
    ALERTS("Alerts, Frame & Windows", "/alerts"),
    FRAMES("Alerts, Frame & Windows", "/frames"),
    NESTED_FRAMES("Alerts, Frame & Windows", "/nestedframes"),
    WEB_TABLES("Elements", "/webtables"),
    LINKS("Elements", "/links"),
    SLIDER("Widgets", "/slider"),
    PROGRESS_BAR("Widgets", "/progress-bar"),
    DATE_PICKER("Widgets", "/date-picker");

    private final String group;
    private final String href;

    Category(String group, String href) {
        this.group = group;
        this.href = href;
    }

    public String getGroup() {
        return group;
    }
    public String getHref() {
        return href;
    }
}