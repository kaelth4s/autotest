package ru.kaelth4s.framework.element;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kaelth4s.framework.base.BaseElement;
import ru.kaelth4s.framework.dto.BaseTableData;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

public class Table extends BaseElement {
    private static final Logger log = LoggerFactory.getLogger(Table.class);

    public Table(By locator, String elementName) {
        super(locator, elementName);
    }

    public <T extends BaseTableData> List<T> getRowDatas(Class<T> tClass)
    {
        log.info("Getting data from rows in table {}", elementName);
        List<String> headers = getHeadersText();
        List<T> bodyRowDatas = new LinkedList<>();
        List<WebElement> bodyRowElements = findElement().findElements(By.xpath(".//tbody//tr"));
        for (int i = 0; i < bodyRowElements.size(); i++) {
            List<WebElement> bodyRowColumns = bodyRowElements.get(i).findElements(By.xpath(".//td"));

            if (bodyRowColumns.isEmpty() || bodyRowColumns.getFirst().getText().trim().isEmpty())
                continue;

            T instance;
            try {
                instance = tClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException    | IllegalAccessException |
                     InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            instance.fillFromRow(bodyRowColumns, headers, i);
            bodyRowDatas.add(instance);
        }
        return bodyRowDatas;
    }

    private List<String> getHeadersText() {
        List<WebElement> headColumns = getHead();
        List<String> headColumnsText = new LinkedList<>();

        for (WebElement headColumn : headColumns) {
            headColumnsText.add(headColumn.getText());
        }

        return headColumnsText;
    }

    private List<WebElement> getHead() {
        return findElement().findElements(By.xpath(".//thead//th"));
    }
}