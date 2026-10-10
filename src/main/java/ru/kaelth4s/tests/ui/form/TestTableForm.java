package ru.kaelth4s.tests.ui.form;

import aquality.selenium.elements.interfaces.IButton;
import aquality.selenium.elements.interfaces.ILabel;
import aquality.selenium.elements.interfaces.ILink;
import aquality.selenium.forms.Form;
import org.openqa.selenium.By;
import ru.kaelth4s.tests.ui.dto.TestTableData;
import ru.kaelth4s.tests.ui.enums.TestTableColumn;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static aquality.selenium.browser.AqualityServices.getConditionalWait;

public class TestTableForm extends Form {
    private static final By TABLE_WRAPPER_LOCATOR = By.className("table");
    private static final By ALL_ROWS_LOCATOR = By.xpath(".//tbody/tr");
    private static final By HEADER_CELLS_LOCATOR = By.xpath(".//th");
    private static final By CELLS_IN_ROW_LOCATOR = By.xpath(".//td");
    private static final By LINK_LOCATOR = By.xpath(".//a");

    private static final DateTimeFormatter TEST_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.S]");
    private static final DateTimeFormatter TEST_DURATION_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss[.SSS]");

    private final IButton tableWrapper = getElementFactory()
            .getButton(TABLE_WRAPPER_LOCATOR, "Table Wrapper");

    public TestTableForm() {
        super(TABLE_WRAPPER_LOCATOR, "Test Table");
    }

    public List<TestTableData> getRows() {
        state().waitForDisplayed();
        List<ILabel> rows = getAllRows();
        List<String> headers = getColumnHeaders();

        return rows.stream()
                .skip(1)
                .map(row -> row.findChildElements(CELLS_IN_ROW_LOCATOR, ILabel.class))
                .filter(cells -> cells.size() == headers.size())
                .map(cells -> buildRow(cells, headers))
                .toList();
    }

    public void clickTestLink(TestTableData row) {
        clickLink(row, TestTableColumn.TEST_NAME);
    }

    public boolean waitForNewLastRow(List<TestTableData> previousRows) {
        return getConditionalWait().waitFor(() -> {
            List<TestTableData> currentRows = getRows();
            if (currentRows.isEmpty()) {
                return false;
            }
            TestTableData current = currentRows.getFirst();
            if (previousRows.isEmpty()) {
                return true;
            }
            TestTableData previous = previousRows.getFirst();
            boolean isNameEquals = !previous.testName().equals(current.testName());
            if (previous.latestTestStartTime() == null || current.latestTestStartTime() == null) {
                return isNameEquals;
            }
            return previous.latestTestStartTime().isBefore(current.latestTestStartTime()) || isNameEquals;
        });
    }

    private void clickLink(TestTableData row, TestTableColumn column) {
        int index = findRowIndex(row);
        link(getRowElements(index), getColumnHeaders(), column).click();
    }

    private int findRowIndex(TestTableData target) {
        List<TestTableData> rows = getRows();
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).testName().equals(target.testName())) {
                return i;
            }
        }
        throw new IllegalStateException("Row not found by test name: %s".formatted(target.testName()));
    }

    private TestTableData buildRow(List<ILabel> cells, List<String> headers) {
        return new TestTableData(
                text(cells, headers, TestTableColumn.TEST_NAME),
                text(cells, headers, TestTableColumn.TEST_METHOD),
                text(cells, headers, TestTableColumn.TEST_RESULT),
                parseDateTime(text(cells, headers, TestTableColumn.START_TIME)),
                parseDateTime(text(cells, headers, TestTableColumn.END_TIME)),
                parseDuration(text(cells, headers, TestTableColumn.DURATION))
        );
    }

    private static LocalDateTime parseDateTime(String text) {
        String value = text.strip();
        return value.isEmpty()
                ? null
                : LocalDateTime.parse(value, TEST_TIME_FORMATTER);
    }

    private static LocalTime parseDuration(String text) {
        String value = text.strip();
        return value.isEmpty()
                ? null
                : LocalTime.parse(value, TEST_DURATION_FORMATTER);
    }

    private List<String> getColumnHeaders() {
        return getAllRows().getFirst()
                .findChildElements(HEADER_CELLS_LOCATOR, ILabel.class)
                .stream()
                .map(ILabel::getText)
                .toList();
    }

    private List<ILabel> getRowElements(int rowIndex) {
        return getAllRows().get(rowIndex + 1)
                .findChildElements(CELLS_IN_ROW_LOCATOR, ILabel.class);
    }

    private List<ILabel> getAllRows() {
        return tableWrapper.findChildElements(ALL_ROWS_LOCATOR, ILabel.class);
    }

    private ILabel cell(List<ILabel> cells, List<String> headers, TestTableColumn column) {
        int index = headers.indexOf(column.getTitle());
        if (index < 0) {
            throw new IllegalStateException("Column '%s' not found. Available: %s".formatted(column.getTitle(), headers));
        }
        return cells.get(index);
    }

    private String text(List<ILabel> cells, List<String> headers, TestTableColumn column) {
        return cell(cells, headers, column).getText();
    }

    private ILink link(List<ILabel> cells, List<String> headers, TestTableColumn column) {
        return cell(cells, headers, column).findChildElement(LINK_LOCATOR, ILink.class);
    }
}