package ru.kaelth4s.tests.ui.page;

import aquality.selenium.forms.Form;
import org.openqa.selenium.By;

public class UnauthorizedMainPage extends Form {
    public UnauthorizedMainPage() {
        super(By.xpath("//link[contains(@rel,'canonical') and contains(@href,'https://360.yandex.ru/disk/')]"), "Unauthorized Main Page");
    }
}