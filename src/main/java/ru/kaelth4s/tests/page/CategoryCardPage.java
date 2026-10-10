package ru.kaelth4s.tests.page;

import org.openqa.selenium.By;
import ru.kaelth4s.framework.base.BaseCategoryForm;

public class CategoryCardPage extends BaseCategoryForm {
    public CategoryCardPage() {
        super(By.className("left-pannel"), "Category Card Page");
    }
}