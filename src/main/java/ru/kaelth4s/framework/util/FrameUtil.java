package ru.kaelth4s.framework.util;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kaelth4s.framework.element.Button;

import static ru.kaelth4s.framework.manager.DriverManager.getDriver;
import static ru.kaelth4s.framework.util.WaitUtil.*;

public class FrameUtil {
    private static final Logger log = LoggerFactory.getLogger(FrameUtil.class);
    private static int frameDepth = 0;

    private FrameUtil() {}

    public static void switchToPage() {
        log.info("Switch driver focus from frame to page");
        if (frameDepth == 0) throw new IllegalStateException("Driver already has the focus on the page");
        getDriver().switchTo().defaultContent();
        frameDepth = 0;
    }

    public static void switchToFrame(String frameId) {
        log.info("Switch driver focus to frame with id '{}'", frameId);
        getDriver().switchTo().frame(frameId);
        frameDepth++;
    }

    public static void switchToFirstFrame() {
        log.info("Switch driver focus to first child frame");
        getDriver().switchTo().frame(waitVisibles(By.tagName("iframe")).getFirst());
        frameDepth++;
    }

    public static String getCurrentFrameText() {
        log.info("Getting current frame text");
        if (frameDepth == 0) throw new IllegalStateException("Driver does not have frame focus");
        return new Button(By.tagName("body"), String.format("Body element in frame with depth '%s'", frameDepth)).getAllText();
    }

    public static boolean waitCurrentFrameTextToBe(String text) {
        log.info("Waiting current frame text to be '{}'", text);
        if (frameDepth == 0) throw new IllegalStateException("Driver does not have frame focus");
        return waitTextToBe(By.tagName("body"), text);
    }
}