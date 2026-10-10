package ru.kaelth4s.page;

import org.openqa.selenium.By;
import ru.kaelth4s.BasePage;


public class ListingPage extends BasePage {
    By listingDescriptionLocator;
    String listingDescriptionLocatorTemplate = "//*[contains(@style,\"text-body-note\") and contains(text(),\"%s\")]//ancestor::*[contains(@style,\"background\")]";

    public ListingPage(String listingName) {
        listingDescriptionLocator = By.xpath(String.format(listingDescriptionLocatorTemplate, listingName));
    }

    @Override
    protected By getUniqueElementLocator() {
        return listingDescriptionLocator;
    }

    public String getListingDescription() { return getText(listingDescriptionLocator); }

}
