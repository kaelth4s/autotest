package ru.kaelth4s.tests.ui.page;

import aquality.selenium.elements.interfaces.IButton;
import aquality.selenium.elements.interfaces.ILabel;
import aquality.selenium.forms.Form;
import org.openqa.selenium.By;
import ru.kaelth4s.tests.ui.enums.Attribute;
import ru.kaelth4s.tests.ui.util.WebUtil;
import ru.kaelth4s.tests.ui.dto.TestPageData;
import ru.kaelth4s.tests.ui.enums.TestPageField;

import javax.swing.text.html.HTML;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.Map;

public class TestPage extends Form {
    private static final By INFO_ITEMS_LOCATOR = By.xpath("//*[contains(@class,'panel-default') and not(contains(@class,'fail-reason-block'))]//*[contains(@class,'list-group-item-heading')]/parent::*");
    private static final By ITEM_HEADERS_LOCATOR = By.xpath(".//*[contains(@class,'list-group-item-heading')]");
    private static final By ITEM_VALUES_LOCATOR = By.xpath(".//*[contains(@class,'list-group-item-text')]");

    private static final String TIME_INFO_TITLE = "Time info";
    private static final String START_TIME_PREFIX = "Start time:";
    private static final String END_TIME_PREFIX = "End time:";
    private static final String DURATION_PREFIX = "Duration (H:m:s.S):";

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
    private static final DateTimeFormatter DURATION_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private final IButton screenshot = getElementFactory()
            .getButton(By.xpath("//*[contains(@class,'thumbnail')]"), "Screenshot");

    public TestPage() {
        super(INFO_ITEMS_LOCATOR, "Test Page");
    }

    private static String trim(String text, String prefix) {
        return text.substring(prefix.length()).strip();
    }

    public TestPageData getData() {
        Map<TestPageField, String> fields = new EnumMap<>(TestPageField.class);
        String startTime = null;
        String endTime = null;
        String duration = null;

        for (ILabel item : getElementFactory().findElements(INFO_ITEMS_LOCATOR, ILabel.class)) {
            String title = item.findChildElement(ITEM_HEADERS_LOCATOR, ILabel.class).getText().strip();

            if (TIME_INFO_TITLE.equals(title)) {
                for (ILabel value : item.findChildElements(ITEM_VALUES_LOCATOR, ILabel.class)) {
                    String text = value.getText().strip();
                    if (text.startsWith(START_TIME_PREFIX)) {
                        startTime = trim(text, START_TIME_PREFIX);
                    } else if (text.startsWith(END_TIME_PREFIX)) {
                        endTime = trim(text, END_TIME_PREFIX);
                    } else if (text.startsWith(DURATION_PREFIX)) {
                        duration = trim(text, DURATION_PREFIX);
                    }
                }
                continue;
            }

            TestPageField field = TestPageField.findByTitleOrNull(title);
            if (field == null) {
                continue;
            }

            String value = item.findChildElement(ITEM_VALUES_LOCATOR, ILabel.class).getText().strip();
            fields.put(field, value);
        }

        return new TestPageData(
                fields.get(TestPageField.PROJECT_NAME),
                fields.get(TestPageField.TEST_NAME),
                fields.get(TestPageField.TEST_METHOD_NAME),
                fields.get(TestPageField.STATUS),
                LocalDateTime.parse(startTime, DATE_TIME_FORMATTER),
                LocalDateTime.parse(endTime, DATE_TIME_FORMATTER),
                LocalTime.parse(duration, DURATION_FORMATTER),
                fields.get(TestPageField.ENVIRONMENT),
                fields.get(TestPageField.BROWSER)
        );
    }

    public byte[] getScreenshot() {
        String src = screenshot.getAttribute(Attribute.SRC.getName());
        return WebUtil.decodeDataUri(src);
    }
}