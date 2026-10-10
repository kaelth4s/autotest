package ru.kaelth4s.tests.page.card;

import aquality.selenium.core.elements.ElementState;
import aquality.selenium.elements.interfaces.IButton;
import aquality.selenium.elements.interfaces.ICheckBox;
import org.openqa.selenium.By;
import ru.kaelth4s.framework.enums.Attribute;
import ru.kaelth4s.framework.util.WebUtil;
import ru.kaelth4s.tests.page.CardPage;

import java.util.List;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

public class InterestsCardPage extends CardPage {
    private final String INTEREST_CHECKBOX_BUTTON_LOCATOR_TEMPLATE = "//label[@for='%s']//*[contains(@class,'checkbox__box')]";
    private final IButton uploadImageButton = getElementFactory()
            .getButton(By.className("avatar-and-interests__upload-button"), "Upload Image");
    private final ICheckBox unselectAllCheckbox = getElementFactory()
            .getCheckBox(By.xpath("//input[@id='interest_unselectall']"), "Unselect All", ElementState.EXISTS_IN_ANY_STATE);
    private final List<ICheckBox> interestCheckboxes = getElementFactory()
            .findElements(By.xpath("//input[contains(@id,'interest') and not(contains(@id,'unselectall')) and not(contains(@id,'selectall'))]"), ICheckBox.class, ElementState.EXISTS_IN_ANY_STATE);
    private final IButton nextButton = getElementFactory()
            .getButton(By.xpath("//*[contains(text(), 'Next')]"), "Next");

    public InterestsCardPage() {
        super(By.className("avatar-and-interests-page"), "Interests Card Page");
    }

    public void unselectAllCheckboxes() {
        clickInterestButtonByCheckbox(unselectAllCheckbox);
    }

    public void uploadImage(String fileResourcePath) {
        WebUtil.uploadFile(uploadImageButton, fileResourcePath);
    }

    public void selectRandomInterestCheckbox() {
        List<Integer> availableIndices = IntStream.range(0, getInterestsCount())
                .filter(index -> !interestCheckboxes.get(index).isChecked())
                .boxed()
                .toList();

        if (availableIndices.isEmpty()) {
            throw new IllegalStateException("All interest checkboxes are already selected");
        }

        int randomIndex = availableIndices.get(RandomGenerator.getDefault().nextInt(availableIndices.size()));
        clickInterestButtonByCheckbox(interestCheckboxes.get(randomIndex));
    }

    public void selectRandomInterestCheckboxes(int count) {
        for (int i = 0; i < count; i++) {
            selectRandomInterestCheckbox();
        }
    }

    public void clickNextButton() {
        nextButton.click();
    }

    private void clickInterestButtonByCheckbox(ICheckBox checkBox) {
        String id = checkBox.getAttribute(Attribute.ID.getName());
        getElementFactory()
                .getButton(By.xpath(String.format(INTEREST_CHECKBOX_BUTTON_LOCATOR_TEMPLATE, id)), id)
                .click();
    }

    private int getInterestsCount() {
        return interestCheckboxes.size();
    }
}