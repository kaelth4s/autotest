package ru.kaelth4s.tests.page;

import org.openqa.selenium.By;
import ru.kaelth4s.framework.base.BaseCategoryForm;
import ru.kaelth4s.framework.base.BaseForm;
import ru.kaelth4s.framework.dto.WaitConfig;
import ru.kaelth4s.framework.element.Button;

import static ru.kaelth4s.framework.manager.ConfigManager.getConfigData;
import static ru.kaelth4s.framework.util.ParserUtil.parseInt;
import static ru.kaelth4s.framework.util.WaitUtil.*;
import static ru.kaelth4s.tests.enums.ConfigWaitKey.PROGRESS_BAR;

public class ProgressBarPage extends BaseCategoryForm {
    private final Button startStopButton = new Button(By.id("startStopButton"), "Start Progress Bar Button");

    private final By progressBarLocator = By.xpath("//*[@role='progressbar']");
    private final Button progressBar = new Button(progressBarLocator, "Progress Bar");

    private boolean isStarted = false;

    public ProgressBarPage() {
        super(By.id("progressBarContainer"), "Progress Bar Page");
    }

    public void clickStartButton() {
        if (isStarted) throw new IllegalStateException("Start button already clicked");
        startStopButton.click();
        isStarted = true;
    }

    public void clickStopButton() {
        if (!isStarted) throw new IllegalStateException("Need to click start button before stop button");
        startStopButton.click();
        isStarted = false;
    }

    public void waitForProgressBarValueGreaterThan(int minValue) {
        WaitConfig config = getConfigData().additionalWaits().get(PROGRESS_BAR.getKey());
        waitNumberAttributeGreaterThanOrEqualTo(
                progressBarLocator,
                "aria-valuenow",
                minValue,
                config.timeout(),
                config.pollingMillis()
        );
    }

    public int getProgressBarValue() {
        return parseInt(progressBar.getAttribute("aria-valuenow").trim());
    }
}