package ru.kaelth4s.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ru.kaelth4s.framework.util.WaitUtil.waitAlert;
import static ru.kaelth4s.framework.util.WaitUtil.waitAlertClosed;

public class AlertUtil {
    private static final Logger log = LoggerFactory.getLogger(AlertUtil.class);

    private AlertUtil() {}

    public static String getAlertText() {
        log.info("Getting the alert text");
        return waitAlert().getText();
    }

    public static void acceptAlert() {
        log.info("Accepting the alert");
        waitAlert().accept();
    }

    public static void inputAlert(String value) {
        log.info("Type '{}' text into the alert input field", value);
        waitAlert().sendKeys(value);
    }

    public static boolean isAlertClosed() {
        log.info("Checking is the alert has closed");
        return waitAlertClosed();
    }
}