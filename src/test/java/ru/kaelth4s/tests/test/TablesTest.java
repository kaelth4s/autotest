package ru.kaelth4s.tests.test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kaelth4s.framework.base.BaseTest;
import ru.kaelth4s.tests.dto.RegistrationData;
import ru.kaelth4s.tests.enums.Category;
import ru.kaelth4s.tests.page.CategoryCardPage;
import ru.kaelth4s.tests.page.MainPage;
import ru.kaelth4s.tests.page.WebTablesPage;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.kaelth4s.tests.manager.TestDataManager.getTestData;

public class TablesTest extends BaseTest {

    private static final Logger log = LoggerFactory.getLogger(TablesTest.class);

    private static Stream<RegistrationData> registrationDataProvider() {
        return getTestData().tablesTestData().userRegistrationData().stream();
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("registrationDataProvider")
    void testTables(RegistrationData registrationData) {
        log.info("Table test '{}' has been launched", registrationData);
        log.info("Step 1. Go to the main page");

        MainPage mainPage = new MainPage();

        assertThat(mainPage.isDisplayed())
                .as("The main page is open")
                .isTrue();

        log.info("Step 2. Click on the Elements button. On the page that opens, click Web Tables in the left menu");

        mainPage.clickElementsButton();
        CategoryCardPage elementsPage = new CategoryCardPage();
        elementsPage.getLeftPanelForm().clickCategory(Category.WEB_TABLES);
        WebTablesPage webTablesPage = new WebTablesPage();

        assertThat(webTablesPage.isDisplayed())
                .as("The Web Tables form page is open")
                .isTrue();

        log.info("Step 3. Click on the Add button");

        webTablesPage.clickAddNewRecordButton();

        assertThat(webTablesPage.getRegistrationForm().isDisplayed())
                .as("The Registration Form appeared on the page.")
                .isTrue();

        log.info("Step 4. Enter user data User № from the test data and then click on the Submit button");

        webTablesPage.getRegistrationForm().enterRegistrationData(registrationData);
        webTablesPage.getRegistrationForm().clickSubmitButton();

        assertThat(webTablesPage.getRegistrationForm().isDisplayed())
                .as("The registration form is closed")
                .isFalse();
        assertThat(webTablesPage.getRows().contains(registrationData))
                .as("The data of the user User No appeared in the table.")
                .isTrue();

        log.info("Step 5. Click on the Delete button in the line of the User");

        int oldRowsCount = webTablesPage.getRows().size();
        webTablesPage.deleteRow(registrationData);

        assertThat(webTablesPage.getRows().size())
                .as("The number of records in the table has changed")
                .isNotEqualTo(oldRowsCount);
        assertThat(webTablesPage.getRows().contains(registrationData))
                .as("The user has been deleted from the table")
                .isFalse();
    }
}