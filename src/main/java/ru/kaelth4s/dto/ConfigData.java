package ru.kaelth4s.dto;

public record ConfigData(int explicitWaitsDuration, String browserName, ChromeConfig chromeConfig, String url) {}
