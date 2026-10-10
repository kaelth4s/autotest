package ru.kaelth4s.tests.page;

import aquality.selenium.forms.Form;
import org.openqa.selenium.By;
import ru.kaelth4s.tests.form.CookiesForm;
import ru.kaelth4s.tests.form.HelpForm;

public class CardPage extends Form {
    private HelpForm helpForm;
    private CookiesForm cookiesForm;

    protected CardPage(By locator, String name) {
        super(locator, name);
    }

    public HelpForm getHelpForm() {
        if (helpForm != null) return helpForm;
        helpForm = new HelpForm();
        return helpForm;
    }

    public CookiesForm getCookiesForm() {
        if (cookiesForm != null) return cookiesForm;
        cookiesForm = new CookiesForm();
        return cookiesForm;
    }
}
