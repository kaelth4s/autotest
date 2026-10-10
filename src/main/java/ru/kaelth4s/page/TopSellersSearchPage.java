package ru.kaelth4s.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import ru.kaelth4s.BasePage;
import ru.kaelth4s.object.Game;
import ru.kaelth4s.page.component.FilterCollapseComponent;

import java.util.List;

public class TopSellersSearchPage extends BasePage {
    By searchResultListContainerLocator = By.id("search_resultsRows");
    By searchResultContainerLocator = By.id("search_result_container");

    By searchResultCountLocator = By.id("search_results");
    By searchResultWarningCountLocator = By.id("search_results_filtered_warning_persistent");

    By searchedGameNameLocator = By.className("search_name");
    By searchedGameReleasedLocator = By.className("search_released");
    By searchedGamePriceLocator = By.className("search_discount_block");

    By topSellersPageHeaderLocator = By.className("pageheader");

    public TopSellersSearchPage() {}

    @Override
    protected By getUniqueElementLocator() {
        return topSellersPageHeaderLocator;
    }

    public GamePage openSearchedGame(int order) {
        click(getSearchedGameList().get(order - 1));
        return new GamePage();
    }

    public Game getSearchedGameByOrder(int order) {
        return new Game(getSearchedGameNameText(order), getSearchedGameReleasedText(order), getSearchedGamePrice(order));
    }

    public int getSearchedGamesCount() { return getSearchedGameList().size(); }

    public String getSearchResultCountText() {
        return getText(getVisibleSearchResultCountLocator());
    }

    public int getSearchResultCount() {
        return Integer.parseInt(getSearchResultCountText().split(" ")[0].replace(",", "").trim());
    }

    public void clickFilterCheckbox(String filterName, String checkboxName) {;
        clickCheckboxWithWait(getFilterCollapseComponentByName(filterName), checkboxName);
    }

    public boolean isCheckboxChecked(String filterName, String checkboxName) {
        return getFilterCollapseComponentByName(filterName).isCheckboxCheckedByName(checkboxName);
    }

    private WebElement getSearchedGameElementByOrder(int order) {
        List<WebElement> searchedGames = getSearchedGameList();
        return searchedGames.get(order - 1);
    }

    private List<WebElement> getSearchedGameList() {
        return waitPresent(searchResultListContainerLocator).findElements(By.xpath("./child::*"));
    }

    private String getSearchedGameNameText(int order) {
        return getSearchedGameElementByOrder(order).findElement(searchedGameNameLocator).getText();
    }

    private String getSearchedGameReleasedText(int order) {
        return getSearchedGameElementByOrder(order).findElement(searchedGameReleasedLocator).getText();
    }

    private int getSearchedGamePrice(int order) {
        WebElement element = getSearchedGameElementByOrder(order).findElement(searchedGamePriceLocator);
        String price = element.getAttribute("data-price-final");
        return price != null ? Integer.parseInt(price) : 0;
    }

    private FilterCollapseComponent getFilterCollapseComponentByName(String filterName) {
        return new FilterCollapseComponent(filterName);
    }

    private void clickCheckboxWithWait(FilterCollapseComponent filterCollapseComponent, String checkboxName) {
        String oldResultCountText = getText(getVisibleSearchResultCountLocator());
        String oldResultContainerText = getText(searchResultListContainerLocator);

        filterCollapseComponent.clickFilterByName(checkboxName);

        waitNotContainsAttribute(searchResultContainerLocator, "style");
        waitTextChanged(getVisibleSearchResultCountLocator(), oldResultCountText);
        waitTextChanged(searchResultListContainerLocator, oldResultContainerText);
    }

    private By getVisibleSearchResultCountLocator() {
        if (isDisplayedElementLocated(searchResultWarningCountLocator)) return searchResultWarningCountLocator;
        return searchResultCountLocator;
    }
}
