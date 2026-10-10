package ru.kaelth4s.test;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.kaelth4s.TestDataManager;
import ru.kaelth4s.base.BaseTest;
import ru.kaelth4s.dto.Filter;
import ru.kaelth4s.page.ListingPage;
import ru.kaelth4s.page.MarketPage;
import ru.kaelth4s.page.MarketSearchPage;
import ru.kaelth4s.page.StorePage;

import java.util.List;

public class MarketTest extends BaseTest {
    @Test
    public void testMarket() {
        StorePage storePage = openUrl();

        MarketPage marketPage = storePage.clickMarketButton();

        Assertions.assertThat(marketPage.isPageOpened())
                .as("Проверка, что страница Community Market открыта")
                .isTrue();

        marketPage.clickAdvancedOptionsButton();

        Assertions.assertThat(marketPage.isAdvancedOptionsFormOpened())
                .as("Проверка, что форма SEARCH COMMUNITY MARKET открыта")
                .isTrue();

        for (Filter filter : TestDataManager.getMarketTestData().filters()) {
            marketPage.clickGameFilter(filter.filterName(), filter.filterValue());
        }
        marketPage.enterSearchInput(TestDataManager.getMarketTestData().search());

        MarketSearchPage marketSearchPage = marketPage.clickSearchButton();

        Assertions.assertThat(marketSearchPage.isPageOpened())
                .as("Проверка, что страница Market Search открыта")
                .isTrue();

        for (Filter filter : TestDataManager.getMarketTestData().filters()) {
            Assertions.assertThat(marketSearchPage.getFilterContainerText().toLowerCase().contains(filter.filterValue().toLowerCase()))
                    .as("Проверка, что поиск содержит выбранный фильтр: " + filter.filterValue())
                    .isTrue();
        }

        if (!TestDataManager.getMarketTestData().search().isBlank()) {
            Assertions.assertThat(marketSearchPage.getFilterList().contains(TestDataManager.getMarketTestData().search()))
                    .as("Проверка, что поиск содержит написанный фильтр поиска")
                    .isTrue();

            for (int i = 1; i < 6; i++) {
                Assertions.assertThat(marketSearchPage.getListingNameByOrder(i).toLowerCase().contains(TestDataManager.getMarketTestData().search().toLowerCase()))
                        .as("Проверка, что " + i + " результат содержит слово  " + TestDataManager.getMarketTestData().search().toLowerCase() + " в названии")
                        .isTrue();
            }
        }

        String oldFoundResultText = marketSearchPage.getFoundResultText();
        marketSearchPage.removeFilter(TestDataManager.getMarketTestData().search());

        Assertions.assertThat(marketSearchPage.getFoundResultText())
                .as("Проверка, что список предметов обновился")
                .isNotEqualTo(oldFoundResultText);

        String listingName = marketSearchPage.getListingNameByOrder(TestDataManager.getMarketTestData().listingOrder());
        List<String> filters = marketSearchPage.getFilterList();
        ListingPage listingPage = marketSearchPage.clickListingByOrder(TestDataManager.getMarketTestData().listingOrder());

        String listingDescription = listingPage.getListingDescription().toLowerCase();
        Assertions.assertThat(listingDescription.contains(listingName.toLowerCase()))
                .as("Проверка, что название предмета соответствует названию с предыдущей страницы")
                .isTrue();
        for (String filter : filters) {
            Assertions.assertThat(listingDescription.contains(filter))
                    .as("Проверка, что информация на странице предмета соответствует предыдущей странице и содержит фильтр: " + filter)
                    .isTrue();
        }
    }
}
