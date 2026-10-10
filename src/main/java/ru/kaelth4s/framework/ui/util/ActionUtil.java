package ru.kaelth4s.framework.ui.util;

import aquality.selenium.elements.interfaces.IElement;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;

import static aquality.selenium.browser.AqualityServices.getBrowser;
import static aquality.selenium.browser.AqualityServices.getLogger;

public final class ActionUtil {
    private static final int DRAG_AND_DROP_ACTION_DURATION_MILLIS = 500;
    private static final int DRAG_AND_DROP_OFFSET = 10;

    private ActionUtil() {
    }

    public static void dragAndDrop(IElement sourceElement, IElement targetElement) {
        getLogger().info("Drag and drop source '%s' to target '%s'", sourceElement, targetElement);

        Actions actions = new Actions(getBrowser().getDriver());

        actions
                .moveToElement(sourceElement.getElement())
                .clickAndHold()
                .pause(Duration.ofMillis(DRAG_AND_DROP_ACTION_DURATION_MILLIS))
                .moveByOffset(DRAG_AND_DROP_OFFSET, DRAG_AND_DROP_OFFSET)
                .pause(Duration.ofMillis(DRAG_AND_DROP_ACTION_DURATION_MILLIS))
                .moveToElement(targetElement.getElement())
                .pause(Duration.ofMillis(DRAG_AND_DROP_ACTION_DURATION_MILLIS))
                .release()
                .perform();
    }
}