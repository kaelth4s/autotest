package ru.kaelth4s.tests.dto;

import ru.kaelth4s.framework.util.RandomUtil;
import ru.kaelth4s.tests.dto.json.CardsTestDataJson;

public record CardsTestData(
        String password,
        String emailName,
        String emailDomain,
        int interestsCount,
        String imageResourcePath
) {
    public static CardsTestData from(CardsTestDataJson json) {
        String emailName = RandomUtil.getRandomEmailName();
        return new CardsTestData(
                RandomUtil.getRandomPasswordForEmail(emailName),
                emailName,
                RandomUtil.getRandomEmailDomain(),
                json.interestsCount(),
                json.imageResourcePath()
        );
    }
}