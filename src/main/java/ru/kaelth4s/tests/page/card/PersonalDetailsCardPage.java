package ru.kaelth4s.tests.page.card;

import org.openqa.selenium.By;
import ru.kaelth4s.tests.page.CardPage;

public class PersonalDetailsCardPage extends CardPage {
    public PersonalDetailsCardPage() {
        super(By.className("personal-details__content"), "Personal Details Card Page");
    }
}