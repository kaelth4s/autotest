package ru.kaelth4s.page.component;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import ru.kaelth4s.BaseComponent;

import java.util.List;

public class GameFilterComponent extends BaseComponent {
    By filterComboboxLocator;
    By filterInputLocator;
    By filterCheckboxListLocator;

    String fitlerComboboxLocatorTemplate = "//*[@role=\"combobox\" and contains(.,\"%s\")]";
    String filterCheckboxListLocatorTemplate = "//*[@id=\"%s\"]//*[@data-selected]";
    String filterInputLocatorTemplate = "//*[@id=\"%s\"]//input";

    public GameFilterComponent(String filterName) {
        filterComboboxLocator = By.xpath(String.format(fitlerComboboxLocatorTemplate, filterName));
    }

    public void clickFilter(String filterValue) {
        openFilterCombobox();

        if (isPresentElementLocated(filterInputLocator))
            enterKeys(filterInputLocator, filterValue);

        List<WebElement> filters = waitPresentList(filterCheckboxListLocator);
        for (int i = 0; i < filters.size(); i++) {
            if (filters.get(i).getText().toLowerCase().trim().contains(filterValue.toLowerCase())) {
                click(waitPresentList(filterCheckboxListLocator).get(i));
                return;
            }
        }
        throw new IllegalArgumentException("Фильтра '" + filterValue + "' не существует или не был найден");
    }

    private void openFilterCombobox() {
        if (waitVisible(filterComboboxLocator).getAttribute("aria-expanded").equals("false")) click(filterComboboxLocator);

        String ariaControlsId = find(filterComboboxLocator).getAttribute("aria-controls");
        filterInputLocator = By.xpath(String.format(filterInputLocatorTemplate, ariaControlsId));
        filterCheckboxListLocator = By.xpath(String.format(filterCheckboxListLocatorTemplate, ariaControlsId));
    }
}
