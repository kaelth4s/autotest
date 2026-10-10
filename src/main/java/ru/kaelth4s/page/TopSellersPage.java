package ru.kaelth4s.page;

import org.openqa.selenium.By;
import ru.kaelth4s.BasePage;

public class TopSellersPage extends BasePage {
    By browseMoreButtonLocator = By.xpath("//*[contains(@class, SteamCharts)]//*[@type=\"button\"]");

    public TopSellersPage() {
        ;
    }

    @Override
    protected By getUniqueElementLocator() {
        return browseMoreButtonLocator;
    }

    public TopSellersSearchPage clickBrowseMoreButton() {
        scrollToElementLocated(browseMoreButtonLocator);
        click(browseMoreButtonLocator);
        return new TopSellersSearchPage();
    }
}
