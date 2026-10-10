package ru.kaelth4s.page;

import org.openqa.selenium.By;
import ru.kaelth4s.BasePage;
import ru.kaelth4s.page.component.HeaderComponent;

public class StorePage extends BasePage {
    By browseButtonLocator = By.xpath("//*[@role=\"button\" and contains(., \"Browse\")]");
    By topSellersButtonLocator = By.xpath("//*[contains(@class,\"Panel\")]//a[contains(., \"Top Sellers\")]");

    private final HeaderComponent headerComponent;

    public StorePage() {
        headerComponent = new HeaderComponent();
    }

    @Override
    protected By getUniqueElementLocator() {
        return browseButtonLocator;
    }

    public AboutPage clickAboutButton() { return headerComponent.clickAboutButton(); }

    public void clickBrowseButton() {
        click(browseButtonLocator);
    }

    public MarketPage clickMarketButton() { return headerComponent.clickMarketButton(); }

    public TopSellersPage clickTopSellersButton() {
        click(topSellersButtonLocator);
        return new TopSellersPage();
    }

}
