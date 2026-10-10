package ru.kaelth4s.tests.page;

import org.openqa.selenium.By;
import ru.kaelth4s.framework.base.BaseCategoryForm;
import ru.kaelth4s.framework.element.Input;
import ru.kaelth4s.framework.element.Slider;

import static ru.kaelth4s.framework.util.ParserUtil.parseInt;

public class SliderPage extends BaseCategoryForm {
    private final Slider slider = new Slider(By.id("slider"), "SliderPage Slider");
    private final Input sliderInput = new Input(By.id("sliderValue"), "Slider Value Input");

    public SliderPage() {
        super(By.className("range-slider"), "Slider Page");
    }

    public void setSliverValue(int value) {
        slider.setValuePercent(value);
    }

    public int getSliderInputValue() {
        return parseInt(sliderInput.getValue());
    }
}