package ru.kaelth4s.page.component;

import org.openqa.selenium.By;
import ru.kaelth4s.BaseComponent;

public class FilterCollapseComponent extends BaseComponent {
    By filterCollapseLocator;

    String inputCollapseChildLocator = "//input[@type=\"text\"]";
    String filterContainerLocatorTemplate = "//*[@data-collapse-name=\"%s\"]";
    String checkboxLocatorTemplate = "//*[@role=\"button\" and @data-loc=\"%s\"]";
    String filterName;

    public FilterCollapseComponent(String filterName) {
        this.filterName = filterName;
        this.filterCollapseLocator = By.xpath(String.format(filterContainerLocatorTemplate, filterName));
    }

    public void clickFilterByName(String filterValue) {
        openCollapseIfNotOpened();

        if (isPresentElementLocated(buildInputChildLocator()))
            searchInInput(filterValue);

        waitVisible(buildCheckboxLocator(filterValue));
        scrollToElementLocated(buildCheckboxLocator(filterValue));
        click(buildCheckboxLocator(filterValue));
    }

    public void searchInInput(String value) {
        openCollapseIfNotOpened();
        waitVisible(buildInputChildLocator());
        enterKeys(buildInputChildLocator(), value);
    }

    public boolean isCheckboxCheckedByName(String checkboxName) {
        openCollapseIfNotOpened();
        return waitPresent(buildCheckboxLocator(checkboxName)).getAttribute("class").contains("checked");
    }

    private void openCollapseIfNotOpened() {
        if (waitPresent(filterCollapseLocator).getAttribute("class").contains("collapsed")) {
            click(filterCollapseLocator);
        }
    }

    private By buildInputChildLocator() {
        return By.xpath(String.format(filterContainerLocatorTemplate, filterName) + inputCollapseChildLocator);
    }

    private By buildCheckboxLocator(String checkboxName) {
        return By.xpath(String.format(filterContainerLocatorTemplate, filterName) + String.format(checkboxLocatorTemplate, checkboxName));
    }
}
