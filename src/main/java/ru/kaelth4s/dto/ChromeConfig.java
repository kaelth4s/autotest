package ru.kaelth4s.dto;

import org.openqa.selenium.chrome.ChromeOptions;

public record ChromeConfig(ChromeOptions chromeOptions, ChromeAdditionalOptions chromeAdditionalOptions) {}
