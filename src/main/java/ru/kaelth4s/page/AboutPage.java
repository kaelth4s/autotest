package ru.kaelth4s.page;

import org.openqa.selenium.By;
import ru.kaelth4s.BasePage;
import ru.kaelth4s.page.component.HeaderComponent;

public class AboutPage extends BasePage {
    By playersOnlineTextLocator = By.xpath("//*[contains(@class,\"gamers_online\")]/parent::*");
    By playersPlayingTextLocator = By.xpath("//*[contains(@class,\"gamers_in_game\")]/parent::*");
    By playersOnlineContainerLocator = By.className("online_stats");

    private final HeaderComponent headerComponent;

    public AboutPage() {
        headerComponent = new HeaderComponent();
    }

    @Override
    protected By getUniqueElementLocator() {
        return playersOnlineContainerLocator;
    }

    public StorePage clickStoreButton() { return headerComponent.clickStoreButton(); }

    public String getPlayersOnlineText() { return getOwnText(playersOnlineTextLocator); }

    public String getPlayersPlayingText() {
        return getOwnText(playersPlayingTextLocator);
    }
}
