package ru.kaelth4s.db.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kaelth4s.db.mock.TestRunSimulator;
import ru.kaelth4s.db.model.Author;
import ru.kaelth4s.db.model.Project;
import ru.kaelth4s.db.model.TestModel;
import ru.kaelth4s.db.repository.AuthorRepository;
import ru.kaelth4s.db.repository.ProjectRepository;
import ru.kaelth4s.db.repository.TestRepository;
import ru.kaelth4s.framework.base.BaseTest;

import java.util.List;

import static aquality.selenium.browser.AqualityServices.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.kaelth4s.db.config.DbTestDataManager.getDbTestData;

public final class DatabaseTest extends BaseTest {

    private final TestRepository testRepository = new TestRepository();
    private final ProjectRepository projectRepository = new ProjectRepository();
    private final AuthorRepository authorRepository = new AuthorRepository();
    private List<TestModel> copies;
    private List<Long> copyIds;

    @BeforeEach
    void prepareTestData() {
        Project project = projectRepository.getOrCreate(getDbTestData().project());
        Author author = authorRepository.getOrCreate(getDbTestData().author());

        List<Long> originalIds = testRepository.findRandomIdsWithRepeatingDigits(getDbTestData().idsLimit());
        copies = testRepository.copyTestsWithNewOwner(originalIds, project, author);
        copyIds = copies.stream()
                .map(TestModel::getId)
                .toList();
    }

    @Test
    void handleTestData() {
        getLogger().info("Step 1. Imitate test runs using mock utility. Update info about tests in database");
        List<TestModel> oldTestResults = testRepository.findAllByIds(copyIds);
        copies.forEach(TestRunSimulator::simulate);
        testRepository.updateAll(copies);
        List<TestModel> newTestResults = testRepository.findAllByIds(copyIds);
        assertThat(oldTestResults)
                .as("Test results were updated")
                .usingRecursiveComparison()
                .isNotEqualTo(newTestResults);
    }

    @AfterEach
    void cleanUpTestData() {
        testRepository.deleteAllByIds(copyIds);
    }
}