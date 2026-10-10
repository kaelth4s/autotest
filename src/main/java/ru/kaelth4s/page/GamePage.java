package ru.kaelth4s.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import ru.kaelth4s.BasePage;
import ru.kaelth4s.object.Game;

public class GamePage extends BasePage {
    By gameDescriptionSnippetLocator = By.className("game_description_snippet");
    By gameReleasedLocator = By.className("date");
    By gameNameLocator = By.id("appHubAppName");
    By gamePriceLocator = By.xpath("//*[contains(@class, \"game_purchase_action\") and not(@id=\"dlc_purchase_action\")]//*[contains(@class, \"game_purchase_price\") and @data-price-final]");

    private final Game game;

    public GamePage() {
        game = new Game(getNameText(), getRealesedText(), getPrice());
    }

    @Override
    protected By getUniqueElementLocator() {
        return gameDescriptionSnippetLocator;
    }

    public Game getGame() { return game; }

    private String getRealesedText() {
        return getText(gameReleasedLocator);
    }

    private String getNameText() {
        return getText(gameNameLocator);
    }

    private int getPrice() {
        WebElement element = waitPresent(gamePriceLocator);
        String price = element.getAttribute("data-price-final");
        return price != null ? Integer.parseInt(price) : 0;
    }


}
