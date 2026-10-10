package ru.kaelth4s.db.base;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.opentest4j.TestAbortedException;
import ru.kaelth4s.db.enums.TestStatus;
import ru.kaelth4s.db.model.SessionModel;
import ru.kaelth4s.db.model.Status;
import ru.kaelth4s.db.model.TestModel;
import ru.kaelth4s.db.repository.*;
import ru.kaelth4s.db.util.SessionModelUtil;

import java.sql.Timestamp;
import java.util.Optional;

import static aquality.selenium.browser.AqualityServices.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.kaelth4s.db.config.DbTestDataManager.getDbTestData;

public final class DbResultExtension implements BeforeEachCallback, AfterEachCallback {
    private static final ExtensionContext.Namespace NS = ExtensionContext.Namespace.create(DbResultExtension.class);
    private static final String STARTED_AT = "startedAt";
    private static final String UNKNOWN = "unknown";
    private static final String TEST_NAME_TEMPLATE = "%s.%s";

    private final TestRepository testRepository = new TestRepository();
    private final AuthorRepository authorRepository = new AuthorRepository();
    private final ProjectRepository projectRepository = new ProjectRepository();
    private final StatusRepository statusRepository = new StatusRepository();
    private final SessionModelRepository sessionRepository = new SessionModelRepository();

    private static Timestamp toSeconds(long millis) {
        return new Timestamp((millis / 1000) * 1000);
    }

    @Override
    public void beforeEach(ExtensionContext ctx) {
        ctx.getStore(NS).put(STARTED_AT, System.currentTimeMillis());
    }

    @Override
    public void afterEach(ExtensionContext ctx) {
        Long startedAt = ctx.getStore(NS).get(STARTED_AT, Long.class);
        if (startedAt == null) {
            return;
        }

        TestStatus status = resolveStatus(ctx);

        long endedAt = System.currentTimeMillis();

        Status statusEntity = statusRepository.get(status);
        SessionModel session = sessionRepository.getOrCreate(SessionModelUtil.get());

        TestModel result = new TestModel();
        result.setName(ctx.getDisplayName());
        result.setMethodName(ctx.getTestMethod()
                .map(m -> String.format(TEST_NAME_TEMPLATE, m.getDeclaringClass().getSimpleName(), m.getName()))
                .orElse(UNKNOWN));
        result.setEnv(resolveEnv(ctx));
        result.setBrowser(resolveBrowser(ctx));
        result.setProject(projectRepository.getOrCreate(getDbTestData().project()));
        result.setAuthor(authorRepository.getOrCreate(getDbTestData().author()));
        result.setStatus(statusEntity);
        result.setSessionModel(session);
        result.setStartTime(toSeconds(startedAt));
        result.setEndTime(toSeconds(endedAt));

        testRepository.addTestResult(result);
        getLogger().info("Saved test result to DB: name=%s, status=%s", result.getName(), status);

        assertThat(testRepository.getLast())
                .as("Information is added")
                .usingRecursiveComparison()
                .isEqualTo(result);
    }

    private TestStatus resolveStatus(ExtensionContext ctx) {
        Optional<Throwable> failure = ctx.getExecutionException();

        if (failure.isEmpty()) {
            return TestStatus.PASSED;
        }

        Throwable cause = failure.get();
        if (cause instanceof TestAbortedException) {
            return TestStatus.SKIPPED;
        }

        getLogger().error(String.format("Test failed: %s — %s", ctx.getDisplayName(), cause.getMessage()));
        return TestStatus.FAILED;
    }

    private String resolveEnv(ExtensionContext ctx) {
        return ctx.getTestClass()
                .map(c -> c.getAnnotation(DbAware.class))
                .map(DbAware::env).get();
    }

    private String resolveBrowser(ExtensionContext ctx) {
        return ctx.getTestClass()
                .map(c -> c.getAnnotation(DbAware.class))
                .map(DbAware::browser).get();
    }
}