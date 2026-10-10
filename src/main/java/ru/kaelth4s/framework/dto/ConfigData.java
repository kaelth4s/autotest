package ru.kaelth4s.framework.dto;

import ru.kaelth4s.framework.browser.Browser;
import java.util.Map;

public record ConfigData(
        int defaultWaitDuration,
        Map<String, WaitConfig> additionalWaits, // изменяем тип
        String url,
        Browser browser,
        BrowserOptions browserOptions
) {}