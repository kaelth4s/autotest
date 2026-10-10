package ru.kaelth4s.test;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.kaelth4s.TestDataManager;
import ru.kaelth4s.base.BaseTest;
import ru.kaelth4s.dto.Filter;
import ru.kaelth4s.object.Game;
import ru.kaelth4s.page.GamePage;
import ru.kaelth4s.page.StorePage;
import ru.kaelth4s.page.TopSellersPage;
import ru.kaelth4s.page.TopSellersSearchPage;

public class TopSellersTest extends BaseTest {
    @Test
    public void testTopSellersPage() {
        StorePage storePage = openUrl();

        storePage.clickBrowseButton();
        TopSellersPage topSellersPage = storePage.clickTopSellersButton();

        Assertions.assertThat(topSellersPage.isPageOpened())
                .as("Проверка, что открыта страница с продуктами - лидерами продаж")
                .isTrue();

        TopSellersSearchPage topSellersSearchPage = topSellersPage.clickBrowseMoreButton();

        Assertions.assertThat(topSellersSearchPage.isPageOpened())
                .as("Проверка, что открыта страница с продуктами - лидерами продаж, фильтрами и поиском")
                .isTrue();

        for (Filter filter : TestDataManager.getTopSellersTestData().filters()) {
            topSellersSearchPage.clickFilterCheckbox(filter.filterName(), filter.filterValue());
            Assertions.assertThat(topSellersSearchPage.isCheckboxChecked(filter.filterName(), filter.filterValue()))
                    .as("Проверка, что чекбокс " + filter.filterValue() + " выбран")
                    .isTrue();
        }

        Assertions.assertThat(topSellersSearchPage.getSearchResultCount())
                .as("Проверка, что указанное количество результатов по запросу соответствует количеству игр в списке")
                .isEqualTo(topSellersSearchPage.getSearchedGamesCount());

        Game searchedGame = topSellersSearchPage.getSearchedGameByOrder(TestDataManager.getTopSellersTestData().gameOrder());
        GamePage gamePage = topSellersSearchPage.openSearchedGame(TestDataManager.getTopSellersTestData().gameOrder());

        Assertions.assertThat(gamePage.isPageOpened())
                .as("Проверка, что страница с описанием игры открыта")
                .isTrue();
        Assertions.assertThat(searchedGame.matches(gamePage.getGame()))
                .as("Проверка, что информация об игры соответствует данным, полученным на шаге 7")
                .isTrue();
    }
}
