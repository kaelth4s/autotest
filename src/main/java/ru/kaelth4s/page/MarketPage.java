package ru.kaelth4s.page;

import org.openqa.selenium.By;
import ru.kaelth4s.BasePage;
import ru.kaelth4s.page.component.GameFilterComponent;

public class MarketPage extends BasePage {
    By advancedOptionsButtonLocator = By.id("market_search_advanced_show");
    By optionsFormLocator = By.className("DialogContent");

    By searchInputLocator = By.xpath("//form[not(@id=\"market_search\")]//input[@role=\"combobox\"]");
    By searchButtonLocator = By.xpath("//button[@type=\"submit\"]");

    public MarketPage() {}

    @Override
    protected By getUniqueElementLocator() {
        return advancedOptionsButtonLocator;
    }

    public void clickAdvancedOptionsButton() { click(advancedOptionsButtonLocator); }

    public void clickGameFilter(String filterName, String filterValue) {
        getGameFilterComponentByName(filterName).clickFilter(filterValue);
    }

    public void enterSearchInput(String keys) {
        enterKeys(searchInputLocator, keys);
    }

    public MarketSearchPage clickSearchButton() {
        click(searchButtonLocator);
        return new MarketSearchPage();
    }

    public boolean isAdvancedOptionsFormOpened() {
        waitVisible(optionsFormLocator);
        return true;
    }

    private GameFilterComponent getGameFilterComponentByName(String filterName) {
        return new GameFilterComponent(filterName);
    }
}
