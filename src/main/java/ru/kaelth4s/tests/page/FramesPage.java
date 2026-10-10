package ru.kaelth4s.tests.page;

import org.openqa.selenium.By;
import ru.kaelth4s.framework.base.BaseCategoryForm;

import static ru.kaelth4s.framework.util.FrameUtil.*;

public class FramesPage extends BaseCategoryForm {
    private final static String frameOneId = "frame1";
    private final static String frameTwoId = "frame2";

    public FramesPage() {
        super(By.id("frame2"), "Frames Pages");
    }

    public String getFrameOneText() {
        return getFrameText(frameOneId);
    }

    public String getFrameTwoText() {
        return getFrameText(frameTwoId);
    }

    private String getFrameText(String frameId) {
        switchToFrame(frameId);
        String frameText = getCurrentFrameText();
        switchToPage();
        return frameText;
    }
}