package ru.kaelth4s.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import ru.kaelth4s.BasePage;

import java.util.ArrayList;
import java.util.List;

public class MarketSearchPage extends BasePage {
    By filterContainerLocator = By.xpath("//*[contains(text(), \"Filters\") and not(self::script)]/parent::*");
    By priceFilterTextLocator = By.xpath("//*[contains(text(),\"Price\") and not(self::script)]");

    By filterButtonListLocator = By.xpath("//*[contains(text(),\"Found\" ) and not(self::script)]/following::*[@type=\"button\"]");
    By foundResultTextLocator = By.xpath("//*[contains(text(),\"Found\" ) and not(self::script)]");

    By listingListLocator = By.xpath("//a[contains(@style,\"contents\")]/child::*");
    By listingNameLocator = By.xpath(".//*[contains(@style,\"text-body-title\")]");

    String filterButtonLocatorTemplate = "//*[contains(text(),\"Found\" ) and not(self::script)]/following::*[@type=\"button\" and contains(text(),\"%s\")]";

    public MarketSearchPage() {}

    @Override
    protected By getUniqueElementLocator() {
        return priceFilterTextLocator;
    }

    public String getFilterContainerText() {
        return getText(filterContainerLocator);
    }

    public String getListingNameByOrder(int order) { return getListingList().get(order - 1).findElement(listingNameLocator).getText(); }

    public void removeFilter(String filter) {
        List<WebElement> filterButtons = getFilterButtonList();
        for (WebElement filterButton : filterButtons) {
            String filterButtonText = filterButton.getText().replace("\"", "").trim();
            if (filterButtonText.contains(filter)) {
                String oldFoundResultText = getFoundResultText();
                click(By.xpath(String.format(filterButtonLocatorTemplate, filterButtonText)));
                waitTextChanged(foundResultTextLocator, oldFoundResultText);
                return;
            }
        }
        throw new IllegalArgumentException("Фильтра '" + filter + "' не было");
    }

    public ListingPage clickListingByOrder(int order) {
        String listingName = getListingNameByOrder(order);
        click(getListingList().get(order - 1));
        return new ListingPage(listingName);
    }

    public String getFoundResultText() {
        return getText(foundResultTextLocator);
    }

    public List<String> getFilterList() {
        List<WebElement> filterButtons = getFilterButtonList();
        List<String> filters = new ArrayList<>();

        for (WebElement filterButton : filterButtons) {
            filters.add(filterButton.getText().trim().toLowerCase().replace("\"",""));
        }
        return filters;
    }

    private List<WebElement> getListingList() {
        return waitPresentList(listingListLocator);
    }

    private List<WebElement> getFilterButtonList() {
        return waitPresentList(filterButtonListLocator);
    }

}
