package ru.kaelth4s.tests.dto;

import ru.kaelth4s.framework.util.RandomGenerated;

public record AlertsTestData(
        @RandomGenerated("getRandomString") String inputText,
        String seeAlertText,
        String confirmBoxAlertText,
        String confirmInscriptionText,
        String promptBoxAlertText
) {}