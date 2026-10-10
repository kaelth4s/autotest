package ru.kaelth4s.tests.page;

import org.openqa.selenium.By;
import ru.kaelth4s.framework.base.BaseCategoryForm;

import static ru.kaelth4s.framework.util.FrameUtil.*;

public class NestedFramesPage extends BaseCategoryForm {
    private final String parentFrameId = "frame1";

    public NestedFramesPage() {
        super(By.xpath("//*[@id='item-3' and contains(@class,'active')]"), "Nested Frames Page");
    }

    public boolean isParentFrameTextToBe(String text) {
        switchToFrame(parentFrameId);
        return isCurrentFrameTextToBe(text);
    }

    public boolean isChildFrameTextToBe(String text) {
        switchToFrame(parentFrameId);
        switchToFirstFrame();
        return isCurrentFrameTextToBe(text);
    }

    private boolean isCurrentFrameTextToBe(String text) {
        boolean isTextToBe = waitCurrentFrameTextToBe(text);
        switchToPage();
        return isTextToBe;
    }

}