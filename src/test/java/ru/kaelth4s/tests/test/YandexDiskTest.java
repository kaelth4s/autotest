package ru.kaelth4s.tests.test;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import ru.kaelth4s.framework.api.client.ApiResponse;
import ru.kaelth4s.framework.base.BaseTest;
import ru.kaelth4s.framework.config.CookieManager;
import ru.kaelth4s.framework.ui.util.TabUtil;
import ru.kaelth4s.framework.util.FileUtil;
import ru.kaelth4s.tests.api.client.*;
import ru.kaelth4s.tests.api.dto.resources.move.PostResourcesMoveResponse;
import ru.kaelth4s.tests.api.dto.resources.upload.GetResourcesUploadResponse;
import ru.kaelth4s.tests.api.dto.trash.resources.TrashResourcesEmbeddedItem;
import ru.kaelth4s.tests.api.dto.trash.resources.restore.PutTrashResourcesRestoreResponse;
import ru.kaelth4s.tests.api.enums.PathPrefix;
import ru.kaelth4s.tests.api.util.TrashUtil;
import ru.kaelth4s.tests.auth.AuthPrepare;
import ru.kaelth4s.tests.ui.dto.FileListing;
import ru.kaelth4s.tests.ui.page.DocumentFilePage;
import ru.kaelth4s.tests.ui.page.MainPage;
import ru.kaelth4s.tests.ui.page.MediaFilePage;
import ru.kaelth4s.tests.ui.page.UnauthorizedMainPage;
import ru.kaelth4s.tests.util.DiskPathUtil;
import ru.kaelth4s.tests.util.Md5Util;
import ru.kaelth4s.tests.util.RandomUtil;

import java.nio.file.Path;
import java.util.List;

import static aquality.selenium.browser.AqualityServices.getBrowser;
import static aquality.selenium.browser.AqualityServices.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.kaelth4s.tests.api.config.ApiTestDataManager.getApiTestData;
import static ru.kaelth4s.tests.ui.config.UiEnvironmentDataManager.getUiEnvironmentData;

public class YandexDiskTest extends BaseTest {
    private final ResourcesUploadClient resourcesUploadClient = new ResourcesUploadClient();
    private final ResourcesMoveClient resourcesMoveClient = new ResourcesMoveClient();
    private final ResourceClient resourceClient = new ResourceClient();
    private final TrashResourceClient trashResourceClient = new TrashResourceClient();
    private final TrashResourcesRestoreClient trashResourcesRestoreClient = new TrashResourcesRestoreClient();

    @Test
    void shouldPassApiAndUiTest() {
        getLogger().info("Step 1. [UI] Go to the main page and verify unauthorized state");

        getBrowser().goTo(getUiEnvironmentData().baseUrl());
        UnauthorizedMainPage unauthorizedMainPage = new UnauthorizedMainPage();

        assertThat(unauthorizedMainPage.state().waitForExist())
                .as("Unauthorized main page is displayed")
                .isTrue();

        getLogger().info("Step 2. [UI] Authorize using saved cookies");

        if (!CookieManager.isExists()) {
            getLogger().info("Cookies not found start automatic auth");
            AuthPrepare.auth();
        }

        getBrowser().goTo(getUiEnvironmentData().robotsUrl());
        CookieManager.load();
        getBrowser().goTo(getUiEnvironmentData().baseUrl());
        MainPage mainPage = new MainPage();

        assertThat(mainPage.state().waitForDisplayed() && mainPage.waitForListingsLoaded())
                .as("Authorized main page is loaded")
                .isTrue();

        getLogger().info("Step 3. [API] Upload text file with random content, expect %s", HttpStatus.SC_CREATED);

        int oldFileListingsCount = mainPage.getCurrentFileListingsCount();
        Path randomTextFilePath = RandomUtil.writeRandomTextFile();
        ApiResponse<GetResourcesUploadResponse> uploadRandomTextFileLinkResponse = resourcesUploadClient.getUploadFileLink(getApiTestData().uploadTextFileDiskPath());

        assertThat(uploadRandomTextFileLinkResponse.statusCode())
                .as("GET status code %d", HttpStatus.SC_OK)
                .isEqualTo(HttpStatus.SC_OK);

        ApiResponse<Void> uploadFileResponse = resourcesUploadClient.uploadFile(
                randomTextFilePath,
                uploadRandomTextFileLinkResponse
        );

        assertThat(uploadFileResponse.statusCode())
                .as("Upload text file returns %s", HttpStatus.SC_CREATED)
                .isEqualTo(HttpStatus.SC_CREATED);

        getLogger().info("Step 4. [UI] Verify new listing appeared without refreshing");

        assertThat(mainPage.waitForNewListing(oldFileListingsCount))
                .as("New listing appeared")
                .isTrue();

        FileListing lastFileListing = mainPage.getLastFileListing();

        assertThat(getApiTestData().uploadTextFileDiskPath())
                .as("Listing name and extension match uploaded file")
                .contains(lastFileListing.name())
                .contains(lastFileListing.extension());

        getLogger().info("Step 5. [UI] Open document in new tab and verify its content");

        mainPage.openLastFileListingInNewTab();
        DocumentFilePage documentFilePage = new DocumentFilePage();

        assertThat(documentFilePage.getDocumentText())
                .as("Document text matches uploaded content")
                .isEqualTo(FileUtil.readFile(randomTextFilePath));

        getLogger().info("Step 6. [API] Change file extension to '%s', expect %s",
                getApiTestData().changedFileExtension(), HttpStatus.SC_CREATED);

        TabUtil.closeCurrent();
        List<FileListing> oldFileListings = mainPage.getCurrentFileListings();
        ApiResponse<PostResourcesMoveResponse> changeExtensionResponse =
                resourcesMoveClient.changeFileExtension(
                        getApiTestData().uploadTextFileDiskPath(),
                        getApiTestData().changedFileExtension()
                );

        assertThat(changeExtensionResponse.statusCode())
                .as("Change extension returns %s", HttpStatus.SC_CREATED)
                .isEqualTo(HttpStatus.SC_CREATED);

        getLogger().info("Step 7. [UI] Verify extension changed without refreshing");

        assertThat(mainPage.waitForListingChanges(oldFileListings))
                .as("Listing changed")
                .isTrue();

        assertThat(mainPage.getLastFileListing().extension())
                .as("Extension changed to '%s'", getApiTestData().changedFileExtension())
                .isEqualTo(getApiTestData().changedFileExtension());

        getLogger().info("Step 8. [API] Upload image with same name (overwrite), expect %s",
                HttpStatus.SC_CREATED);

        String changedDiskPath = DiskPathUtil.replaceExtension(
                getApiTestData().uploadTextFileDiskPath(),
                getApiTestData().changedFileExtension()
        );
        ApiResponse<GetResourcesUploadResponse> uploadImageFileLinkResponse = resourcesUploadClient.getUploadFileLink(changedDiskPath);

        assertThat(uploadImageFileLinkResponse.statusCode())
                .as("GET status code %d", HttpStatus.SC_OK)
                .isEqualTo(HttpStatus.SC_OK);

        ApiResponse<Void> uploadImageResponse = resourcesUploadClient.uploadResourceFile(
                getApiTestData().uploadImageFileResourcePath(),
                uploadImageFileLinkResponse
        );

        assertThat(uploadImageResponse.statusCode())
                .as("Upload image returns %s", HttpStatus.SC_CREATED)
                .isEqualTo(HttpStatus.SC_CREATED);

        getLogger().info("Step 9. [UI] Refresh page, open image and verify content via MD5");

        TabUtil.refreshTab();
        mainPage.waitForListingsLoaded();
        mainPage.openLastFileListing();
        MediaFilePage mediaFilePage = new MediaFilePage();
        String localMd5 = Md5Util.ofBytes(
                FileUtil.readResourceBytes(getApiTestData().uploadImageFileResourcePath()));
        String remoteMd5 = resourceClient.getMetadata(changedDiskPath).body().md5();

        assertThat(mediaFilePage.state().waitForDisplayed())
                .as("Image preview is displayed")
                .isTrue();

        assertThat(remoteMd5)
                .as("Image MD5 on Disk matches uploaded file")
                .isEqualTo(localMd5);

        getLogger().info("Step 10. [UI] Drag image listing to trash");

        mediaFilePage.closeMedia();
        List<FileListing> fileListingsBeforeTrash = mainPage.getCurrentFileListings();
        mainPage.moveLastFileListingToTrash();

        assertThat(mainPage.waitForListingChanges(fileListingsBeforeTrash))
                .as("Image disappeared from listings")
                .isTrue();

        getLogger().info("Step 11. [API] Verify file is in trash");

        String expectedOriginPath = PathPrefix.DISK.apply(changedDiskPath);
        TrashResourcesEmbeddedItem trashItem = TrashUtil.waitForItemByOriginPath(trashResourceClient, changedDiskPath);

        assertThat(trashItem.originPath())
                .as("Trash item origin path matches deleted file")
                .isEqualTo(expectedOriginPath);

        getLogger().info("Step 12. [API] Restore file from trash, expect %s", HttpStatus.SC_CREATED);

        List<FileListing> fileListingsBeforeRestore = mainPage.getCurrentFileListings();
        ApiResponse<PutTrashResourcesRestoreResponse> restoreResponse =
                trashResourcesRestoreClient.restoreFileFromTrash(trashItem.path());

        assertThat(restoreResponse.statusCode())
                .as("Restore from trash returns %s", HttpStatus.SC_CREATED)
                .isEqualTo(HttpStatus.SC_CREATED);

        getLogger().info("Step 13. [UI] Verify image is back without refreshing");

        assertThat(mainPage.waitForListingChanges(fileListingsBeforeRestore))
                .as("Image is back in listings")
                .isTrue();

        getLogger().info("Step 14. [API] Permanently delete image, expect %s", HttpStatus.SC_NO_CONTENT);

        ApiResponse<Void> deleteResponse = resourceClient.deleteFilePermanently(changedDiskPath);

        assertThat(deleteResponse.statusCode())
                .as("Permanent delete returns %s", HttpStatus.SC_NO_CONTENT)
                .isEqualTo(HttpStatus.SC_NO_CONTENT);

        getLogger().info("Step 15. [UI] Refresh and verify image absent");

        mainPage.moveLastFileListingToTrash();

        assertThat(mainPage.waitForFailedNotification())
                .as("Image removed from listings")
                .isTrue();
    }
}