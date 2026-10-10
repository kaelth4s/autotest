package ru.kaelth4s.tests.page;

import org.openqa.selenium.By;
import ru.kaelth4s.framework.base.BaseCategoryForm;
import ru.kaelth4s.framework.element.Button;
import ru.kaelth4s.framework.element.Table;
import ru.kaelth4s.tests.dto.RegistrationData;
import ru.kaelth4s.tests.form.RegistrationForm;

import java.util.List;

public class WebTablesPage extends BaseCategoryForm {
    private final static String deleteButtonLocatorIdTemplate = "delete-record-";

    private final Button addNewRecordButton = new Button(By.id("addNewRecordButton"), "Add New Record Button");
    private final Table webTable = new Table(By.xpath("//*[contains(@class,'web-tables-wrapper')]//table"), "Web Table");

    private final RegistrationForm registrationForm = new RegistrationForm();

    public WebTablesPage() {
        super(By.className("web-tables-wrapper"), "Web Tables Page");
    }

    public RegistrationForm getRegistrationForm() {
        return registrationForm;
    }

    public void clickAddNewRecordButton() {
        addNewRecordButton.click();
    }

    public void deleteRow(RegistrationData registrationData) {
        List<RegistrationData> rows = getRows();
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).equals(registrationData)) {
                deleteRowByIndex(i);
                return;
            }
        }
    }

    public List<RegistrationData> getRows() {
        return webTable.getRowDatas(RegistrationData.class);
    }

    private void deleteRowByIndex(int index) {
        new Button(By.id(deleteButtonLocatorIdTemplate + (index + 1)), String.format("Delete Button num.%s", (index + 1))).click();
    }
}