package ru.kaelth4s.tests.test;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.OutputType;
import ru.kaelth4s.framework.api.base.ApiResponse;
import ru.kaelth4s.framework.base.BaseUiTest;
import ru.kaelth4s.framework.db.enums.SortType;
import ru.kaelth4s.framework.ui.config.CookieManager;
import ru.kaelth4s.framework.ui.util.BasicAuthUtil;
import ru.kaelth4s.framework.ui.util.TabUtil;
import ru.kaelth4s.framework.util.FileUtil;
import ru.kaelth4s.tests.api.client.TokenGetClient;
import ru.kaelth4s.tests.api.dto.token.get.PostTokenGetResponse;
import ru.kaelth4s.tests.db.enums.AttachmentContentType;
import ru.kaelth4s.tests.db.enums.TestArtifact;
import ru.kaelth4s.tests.db.enums.TestStatus;
import ru.kaelth4s.tests.db.model.Attachment;
import ru.kaelth4s.tests.db.model.Log;
import ru.kaelth4s.tests.db.model.TestModel;
import ru.kaelth4s.tests.db.repository.*;
import ru.kaelth4s.tests.db.util.SessionModelUtil;
import ru.kaelth4s.tests.ui.dto.TestPageData;
import ru.kaelth4s.tests.ui.dto.TestTableData;
import ru.kaelth4s.tests.ui.enums.CookieName;
import ru.kaelth4s.tests.ui.page.HomePage;
import ru.kaelth4s.tests.ui.page.ProjectPage;
import ru.kaelth4s.tests.ui.page.TestPage;

import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static aquality.selenium.browser.AqualityServices.getBrowser;
import static aquality.selenium.browser.AqualityServices.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.kaelth4s.tests.api.config.ApiTestDataManager.getApiTestData;
import static ru.kaelth4s.tests.db.config.DbTestDataManager.getDbTestData;
import static ru.kaelth4s.tests.ui.config.UiEnvironmentDataManager.getUiEnvironmentData;
import static ru.kaelth4s.tests.ui.config.UiTestDataManager.getUiTestData;

public class ExamTest extends BaseUiTest {
    private static final String TEST_NAME_TEMPLATE = "%s%s";
    private static final ChronoUnit TIME_PRECISION = ChronoUnit.SECONDS;

    private final TokenGetClient tokenGetClient = new TokenGetClient();
    private final TestModelRepository testModelRepository = new TestModelRepository();
    private final ProjectRepository projectRepository = new ProjectRepository();
    private final StatusRepository statusRepository = new StatusRepository();
    private final LogRepository logRepository = new LogRepository();
    private final AttachmentRepository attachmentRepository = new AttachmentRepository();

    @Test
    void shouldCreateTestWithLogAndScreenshot() {
        long startTime = System.currentTimeMillis();
        Instant startMoment = Instant.ofEpochMilli(startTime).truncatedTo(TIME_PRECISION);
        String variant = getApiTestData().postTokenGetRequest().variant();

        getLogger().info("Step 1. Requesting a token from the API for the variant number '%s'", variant);

        ApiResponse<PostTokenGetResponse> tokenResponse = tokenGetClient.getToken(getApiTestData().postTokenGetRequest());

        assertThat(tokenResponse.body().token())
                .as("Token is generated")
                .isNotBlank();

        Set<Cookie> cookies = Set.of(new Cookie(CookieName.TOKEN.getName(), tokenResponse.body().token()));
        CookieManager.save(cookies);

        getLogger().info("Step 2. Opening the site, passing the authorization and transferring the token with the '%s' cookie",
                CookieName.TOKEN.getName());

        BasicAuthUtil.apply(getUiEnvironmentData().hostPart(), getUiEnvironmentData().loginAuth(), getUiEnvironmentData().passwordAuth());
        getBrowser().goTo(getUiEnvironmentData().baseUrl());
        TabUtil.refreshTab();

        HomePage homePage = new HomePage();
        assertThat(homePage.state().waitForDisplayed())
                .as("Projects page is opened")
                .isTrue();

        CookieManager.load();
        TabUtil.refreshTab();

        homePage = new HomePage();
        homePage.state().waitForDisplayed();

        assertThat(homePage.getFooterForm().isVersionEqual(variant))
                .as("After the page refresh the footer contains the variant number '%s'", variant)
                .isTrue();

        getLogger().info("Step 3. Opening the '%s' project page and requesting the project test list from the DB",
                getUiTestData().projectName());

        homePage.clickProjectBy(getUiTestData().projectName());
        ProjectPage projectPage = new ProjectPage();
        projectPage.state().waitForDisplayed();

        long projectId = projectRepository
                .getProjectByName(getUiTestData().projectName())
                .getId();

        List<TestModel> testsDb = testModelRepository
                .getAllByProjectSortedByStartTime(projectId, SortType.DESC);
        List<TestTableData> testsUi = projectPage.getTestTableForm().getRows();

        assertThat(testsUi)
                .as("Tests on the first page are sorted by descending start time")
                .extracting(TestTableData::latestTestStartTime)
                .isSortedAccordingTo(Comparator.reverseOrder());

        List<TestModel> latestTestsDb = testsDb.stream()
                .collect(Collectors.toMap(
                        TestModel::getName,
                        Function.identity(),
                        (first, second) -> first.getStartTime().after(second.getStartTime()) ? first : second
                ))
                .values().stream()
                .sorted(Comparator.comparing(TestModel::getStartTime).reversed())
                .limit(testsUi.size())
                .toList();

        assertThat(testsUi)
                .as("Test names on the first page match the DB query result")
                .extracting(TestTableData::testName)
                .containsExactlyElementsOf(
                        latestTestsDb.stream().map(TestModel::getName).toList()
                );

        assertThat(testsUi)
                .as("Test methods on the first page match the DB query result")
                .extracting(TestTableData::testMethod)
                .containsExactlyElementsOf(
                        latestTestsDb.stream().map(TestModel::getMethodName).toList()
                );

        assertThat(testsUi)
                .as("Test start times on the first page match the DB query result")
                .extracting(TestTableData::latestTestStartTime)
                .containsExactlyElementsOf(
                        latestTestsDb.stream()
                                .map(TestModel::getStartTime)
                                .map(Timestamp::toLocalDateTime)
                                .toList()
                );

        getLogger().info("Step 4. Returning to the projects page with the browser back button and adding the '%s' project",
                getUiTestData().newProjectName());

        getBrowser().getDriver().navigate().back();
        homePage = new HomePage();

        assertThat(homePage.getProjectNames().contains(getUiTestData().newProjectName()))
                .as("Project '%s' does not exist in the list before adding", getUiTestData().newProjectName())
                .isFalse();

        homePage.clickAddProjectButton();
        homePage.getAddProjectForm().enterProjectName(getUiTestData().newProjectName());
        homePage.getAddProjectForm().clickSave();

        assertThat(homePage.getAddProjectForm().waitForSuccessAlert())
                .as("Success alert is shown after the project saving")
                .isTrue();

        assertThat(homePage.getAddProjectForm().closePopUp())
                .as("Add project window is closed after the JS method call")
                .isTrue();

        TabUtil.refreshTab();
        homePage = new HomePage();

        assertThat(homePage.getProjectNames().contains(getUiTestData().newProjectName()))
                .as("Project '%s' appeared in the list after the page refresh", getUiTestData().newProjectName())
                .isTrue();

        getLogger().info("Step 5. Opening the created project page and adding a test with a log and a screenshot via the DB");

        homePage.clickProjectBy(getUiTestData().newProjectName());
        projectPage = new ProjectPage();
        projectPage.state().waitForDisplayed();
        List<TestTableData> testsBeforeAdd = projectPage.getTestTableForm().getRows();

        String testName = TEST_NAME_TEMPLATE.formatted(getDbTestData().test().getName(), startTime);

        TestModel test = TestModel.builder()
                .name(testName)
                .methodName(getDbTestData().test().getMethodName())
                .status(statusRepository.getByNameOrCreate(TestStatus.PASSED))
                .sessionModel(SessionModelUtil.get())
                .startTime(Timestamp.from(startMoment))
                .endTime(Timestamp.from(Instant.now().truncatedTo(TIME_PRECISION)))
                .env(getDbTestData().test().getEnv())
                .project(projectRepository.getProjectByName(getUiTestData().newProjectName()))
                .build();
        TestModel createdTest = testModelRepository.create(test);

        byte[] screenshot = getBrowser().getDriver().getScreenshotAs(OutputType.BYTES);

        Log log = new Log();
        log.setContent(FileUtil.readFile(Path.of(TestArtifact.LOG_FILE.getPath())));
        log.setException(false);
        log.setTest(createdTest);

        Attachment attachment = new Attachment();
        attachment.setContent(screenshot);
        attachment.setContentType(AttachmentContentType.PNG.getName());
        attachment.setTest(createdTest);

        logRepository.create(log);
        attachmentRepository.create(attachment);

        assertThat(projectPage.getTestTableForm().waitForNewLastRow(testsBeforeAdd))
                .as("Test '%s' appeared in the table without the page refresh", testName)
                .isTrue();

        getLogger().info("Step 6. Opening the created test page and comparing all fields with the DB data");

        projectPage.getTestTableForm().clickTestLink(projectPage.getTestTableForm().getRows().getFirst());
        TestPage testPage = new TestPage();
        testPage.state().waitForDisplayed();

        TestPageData actualTestPageData = testPage.getData();
        byte[] actualScreenshot = testPage.getScreenshot();

        assertThat(actualTestPageData.projectName())
                .as("Project name matches the DB value")
                .isEqualTo(createdTest.getProject().getName());

        assertThat(actualTestPageData.testName())
                .as("Test name matches the DB value")
                .isEqualTo(createdTest.getName());

        assertThat(actualTestPageData.testMethodName())
                .as("Test method name matches the DB value")
                .isEqualTo(createdTest.getMethodName());

        assertThat(actualTestPageData.status())
                .as("Status matches the DB value")
                .isEqualToIgnoringCase(createdTest.getStatus().getName());

        assertThat(actualTestPageData.startTime())
                .as("Start time matches the DB value")
                .isEqualTo(createdTest.getStartTime().toLocalDateTime());

        assertThat(actualTestPageData.endTime())
                .as("End time matches the DB value")
                .isEqualTo(createdTest.getEndTime().toLocalDateTime());

        assertThat(actualTestPageData.environment())
                .as("Environment matches the DB value")
                .isEqualTo(createdTest.getEnv());

        assertThat(actualTestPageData.browser())
                .as("Browser matches the DB value")
                .isEqualTo(createdTest.getBrowser());

        assertThat(actualTestPageData.duration())
                .as("Duration matches the DB value")
                .isEqualTo(LocalTime.ofSecondOfDay(
                        Duration.between(createdTest.getStartTime().toInstant(),
                                createdTest.getEndTime().toInstant()).getSeconds()));

        assertThat(actualScreenshot)
                .as("Screenshot matches the one sent to the DB")
                .isEqualTo(screenshot);
    }
}