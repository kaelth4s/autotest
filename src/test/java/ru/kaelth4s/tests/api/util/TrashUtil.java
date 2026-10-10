package ru.kaelth4s.tests.api.util;

import org.apache.http.HttpStatus;
import ru.kaelth4s.framework.api.client.ApiResponse;
import ru.kaelth4s.tests.api.client.TrashResourceClient;
import ru.kaelth4s.tests.api.dto.trash.resources.GetTrashResourcesResponse;
import ru.kaelth4s.tests.api.dto.trash.resources.TrashResourcesEmbeddedItem;
import ru.kaelth4s.tests.api.enums.PathPrefix;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

import static aquality.selenium.browser.AqualityServices.getConditionalWait;
import static aquality.selenium.browser.AqualityServices.getLogger;

public final class TrashUtil {
    private TrashUtil() {
    }

    public static TrashResourcesEmbeddedItem waitForItemByOriginPath(
            TrashResourceClient client, String diskPath) {

        String expectedOriginPath = PathPrefix.DISK.apply(diskPath);
        getLogger().info("Waiting for item with origin path '%s' to appear in trash", expectedOriginPath);

        AtomicReference<TrashResourcesEmbeddedItem> found = new AtomicReference<>();

        boolean appeared = getConditionalWait().waitFor(() -> {
            ApiResponse<GetTrashResourcesResponse> response = client.getAllTrashSorted();

            if (response.statusCode() != HttpStatus.SC_OK
                    || response.body() == null
                    || response.body().embedded() == null
                    || response.body().embedded().items().isEmpty()) {
                return false;
            }

            TrashResourcesEmbeddedItem item = response.body().embedded().items().stream()
                    .filter(i -> expectedOriginPath.equals(i.originPath()))
                    .findFirst()
                    .orElse(null);

            found.set(item);
            return item != null;
        });

        if (!appeared) {
            throw new NoSuchElementException("Item with expected '%s' origin disk path does not appear in trash".formatted(expectedOriginPath));
        }

        return found.get();
    }
}