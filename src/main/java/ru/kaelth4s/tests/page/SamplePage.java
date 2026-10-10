package ru.kaelth4s.tests.page;

import org.openqa.selenium.By;
import ru.kaelth4s.framework.base.BaseForm;

public class SamplePage extends BaseForm {
    public SamplePage() {
        super(By.id("sampleHeading"), "Sample Page");
    }
}