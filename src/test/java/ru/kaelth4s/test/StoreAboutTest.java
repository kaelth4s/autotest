package ru.kaelth4s.test;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.kaelth4s.base.BaseTest;
import ru.kaelth4s.page.AboutPage;
import ru.kaelth4s.page.StorePage;

public class StoreAboutTest extends BaseTest {
    @Test
    public void testStoreAboutPages() {
        StorePage storePage = openUrl();

        AboutPage aboutPage = storePage.clickAboutButton();

        Assertions.assertThat(aboutPage.isPageOpened())
                .as("Проверка что страница About открыта")
                .isTrue();

        Integer playersNow = Integer.parseInt(aboutPage.getPlayersPlayingText().replace(",","").trim());
        Integer playersOnline = Integer.parseInt(aboutPage.getPlayersOnlineText().replace(",","").trim());

        Assertions.assertThat(playersNow)
                .as("Проверка, что число игроков сейчас меньше, чем онлайн")
                .isLessThan(playersOnline);

        storePage = aboutPage.clickStoreButton();

        Assertions.assertThat(storePage.isPageOpened())
                .as("Проверка, что открыта страница магазина")
                .isTrue();

    }
}
