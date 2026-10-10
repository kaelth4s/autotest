package ru.kaelth4s.framework.dto;

import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BaseTableData {
    protected Map<String, Integer> columnIndexMap;
    protected List<WebElement> cells;

    public void fillFromRow(List<WebElement> cells, List<String> headers, int rowIndex) {
        this.cells = cells;
        this.columnIndexMap = headers.stream()
                .collect(Collectors.toMap(String::toLowerCase, headers::indexOf));
        fillFields(rowIndex);
    }

    protected abstract void fillFields(int rowIndex);

    protected String getCellText(String columnName) {
        Integer index = columnIndexMap.get(columnName.toLowerCase());
        if (index == null || index >= cells.size()) return null;
        return cells.get(index).getText();
    }
}