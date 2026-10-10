package ru.kaelth4s.page.component;

import org.openqa.selenium.By;
import ru.kaelth4s.BaseComponent;
import ru.kaelth4s.page.AboutPage;
import ru.kaelth4s.page.MarketPage;
import ru.kaelth4s.page.StorePage;

public class HeaderComponent extends BaseComponent {
    By storeButtonLocator = By.xpath("//*[@class=\"supernav_container\"]//*[contains(@data-tooltip-content,\"Store\")]");
    By aboutButtonLocator = By.xpath("//*[@class=\"supernav_container\"]//*[contains(.,\"About\")]");
    By communityPopupLocator = By.xpath("//*[@id=\"global_header\"]//*[@data-tooltip-content=\".submenu_Community\"]");
    By marketButtonLocator = By.xpath("//*[@id=\"tooltip-1\"]//*[contains(text(), \"Market\")]");

    public HeaderComponent() {}

    public StorePage clickStoreButton() {
        click(storeButtonLocator);
        return new StorePage();
    }

    public AboutPage clickAboutButton() {
        click(aboutButtonLocator);
        return new AboutPage();
    }

    public MarketPage clickMarketButton() {
        moveToElement(communityPopupLocator);
        click(marketButtonLocator);
        return new MarketPage();
    }
}
