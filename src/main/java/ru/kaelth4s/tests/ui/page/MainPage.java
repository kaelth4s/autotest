package ru.kaelth4s.tests.ui.page;

import aquality.selenium.elements.interfaces.IButton;
import aquality.selenium.elements.interfaces.ILabel;
import aquality.selenium.forms.Form;
import org.openqa.selenium.By;
import ru.kaelth4s.framework.ui.enums.Attribute;
import ru.kaelth4s.framework.ui.util.ActionUtil;
import ru.kaelth4s.framework.ui.util.TabUtil;
import ru.kaelth4s.tests.ui.dto.FileListing;
import ru.kaelth4s.tests.ui.form.DeleteFileForm;

import java.util.LinkedList;
import java.util.List;

import static aquality.selenium.browser.AqualityServices.getConditionalWait;
import static aquality.selenium.browser.AqualityServices.getLogger;

public class MainPage extends Form {
    private static final Character DOT_CHARACTER = '.';
    private static final By SPINNER_LOCATOR = By.xpath("//*[contains(@class,'Orb-Spinner')]");
    private static final By LISTING_TITLE_LOCATOR = By.className("clamped-text");
    private static final By LISTING_IMAGE_PREVIEW_HREF = By.xpath("//img");

    private final IButton trashButton = getElementFactory()
            .getButton(By.id("/trash"), "Trash");
    private final IButton failedNotificationButton = getElementFactory()
            .getButton(By.className("Orb-Snackbar_feedback_danger"), "Failed Notification");
    private final DeleteFileForm deleteFileForm = new DeleteFileForm();

    public MainPage() {
        super(By.xpath("//*[contains(@class,'client-listing') and not(contains(@class,'client-listing_type'))]"), "Yandex Disk Main Page");
    }

    public void openLastFileListingInNewTab() {
        int oldTabsCount = TabUtil.getTabsCount();
        getCurrentFileListingButtons().getLast().getMouseActions().doubleClick();
        TabUtil.waitNewAndSwitch(oldTabsCount);
    }

    public void openLastFileListing() {
        getCurrentFileListingButtons().getLast().getMouseActions().doubleClick();
    }

    public FileListing getLastFileListing() {
        return getCurrentFileListings().getLast();
    }

    public void moveLastFileListingToTrash() {
        List<IButton> fileListingButtons = getCurrentFileListingButtons();
        ActionUtil.dragAndDrop(fileListingButtons.getLast(), trashButton);
        if (deleteFileForm.state().isDisplayed())
            deleteFileForm.deleteFile();
    }

    public int getCurrentFileListingsCount() {
        return getCurrentFileListings().size();
    }

    public boolean waitForNewListing(int oldFileListingsCount) {
        getLogger().info("Waiting for a new file listing (previous count: %d)", oldFileListingsCount);

        return getConditionalWait().waitFor(
                () -> getCurrentFileListingButtons().size() > oldFileListingsCount
        );
    }

    public boolean waitForListingChanges(List<FileListing> oldFileListings) {
        getLogger().info("Waiting for a file listings changes");

        return getConditionalWait().waitFor(
                () -> !getCurrentFileListings().equals(oldFileListings)
        );
    }

    public boolean waitForListingsLoaded() {
        getLogger().info("Waiting for listings to load (spinner to disappear)");
        return getConditionalWait().waitFor(
                () -> getElementFactory().findElements(SPINNER_LOCATOR, ILabel.class).isEmpty()
        );
    }

    public List<FileListing> getCurrentFileListings() {
        List<FileListing> fileListings = new LinkedList<>();
        List<IButton> fileListingButtons = getCurrentFileListingButtons();
        fileListingButtons.forEach(button -> fileListings.add(from(button)));
        return fileListings;
    }

    public boolean waitForFailedNotification() {
        return failedNotificationButton.state().waitForDisplayed();
    }

    private List<IButton> getCurrentFileListingButtons() {
        return getElementFactory().findElements(By.className("listing-item_type_file"), IButton.class);
    }

    private FileListing from(IButton fileListingButton) {
        String title = fileListingButton
                .findChildElement(LISTING_TITLE_LOCATOR, ILabel.class)
                .getText();
        String imagePreviewHref = fileListingButton
                .findChildElement(LISTING_IMAGE_PREVIEW_HREF, IButton.class)
                .getAttribute(Attribute.SRC.getName());

        int dot = title.lastIndexOf(DOT_CHARACTER);
        String name = (dot > 0) ? title.substring(0, dot) : title;
        String extension = (dot > 0) ? title.substring(dot + 1) : "";

        return new FileListing(name, extension, imagePreviewHref);
    }
}