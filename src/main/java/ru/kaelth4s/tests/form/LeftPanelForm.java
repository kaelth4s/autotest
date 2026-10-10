package ru.kaelth4s.tests.form;

import org.openqa.selenium.By;
import ru.kaelth4s.framework.base.BaseForm;
import ru.kaelth4s.framework.element.Button;
import ru.kaelth4s.framework.enums.Attribute;
import ru.kaelth4s.tests.enums.Category;

public class LeftPanelForm extends BaseForm {
    private final Button leftPanel = new Button(By.className("left-pannel"), "Left Panel Container");

    public LeftPanelForm() {
        super(By.className("left-pannel"), "Left Panel Form");
    }

    public void clickCategory(Category category) {
        Button groupButton;
        groupButton = leftPanel.getChild(
                By.xpath(String.format("//*[contains(@class, 'element-group') and contains(.,'%s')]",
                        category.getGroup())), String.format("%s Element Group Button", category.getGroup()),
                Button.class
        );

        if (!groupButton.getAttribute(Attribute.CLASS).contains("show"))
            groupButton.click();

        Button categoryButton;
        categoryButton = groupButton.getChild(
                By.xpath(String.format(".//a[@href='%s']", category.getHref())),
                String.format("%s Button", category.getHref()),
                Button.class
        );

        categoryButton.click();
    }
}