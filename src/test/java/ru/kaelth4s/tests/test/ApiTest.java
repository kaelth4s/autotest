package ru.kaelth4s.tests.test;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import ru.kaelth4s.framework.base.BaseTest;
import ru.kaelth4s.framework.client.ApiResponse;
import ru.kaelth4s.tests.client.PostClient;
import ru.kaelth4s.tests.client.UserClient;
import ru.kaelth4s.tests.dto.api.posts.CreatePostRequest;
import ru.kaelth4s.tests.dto.api.posts.PostResponse;
import ru.kaelth4s.tests.dto.api.users.UserResponse;
import ru.kaelth4s.tests.enums.ContentType;
import ru.kaelth4s.tests.enums.Endpoint;

import java.util.List;
import java.util.Map;

import static aquality.selenium.browser.AqualityServices.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static ru.kaelth4s.tests.config.TestDataManager.getTestData;


public final class ApiTest extends BaseTest {
    private final PostClient postClient = new PostClient();
    private final UserClient userClient = new UserClient();

    @Test
    void shouldPassApiResponses() {
        getLogger().info("Step 1. Send GET request to get all messages (%s)", Endpoint.POSTS.getPath());
        ApiResponse<List<PostResponse>> postsResponse = postClient.getAll();

        assertThat(postsResponse.statusCode())
                .as("Status code %s", HttpStatus.SC_OK)
                .isEqualTo(HttpStatus.SC_OK);
        assertThat(postsResponse.contentType())
                .as("Response body is json")
                .contains(ContentType.JSON.getValue());
        assertThat(postsResponse.body())
                .as("Posts is sorted")
                .extracting(PostResponse::id)
                .isSorted();

        PostResponse expectedPost = getTestData().expectedPostGetRequest();
        getLogger().info("Step 2. Send GET request to get post with id=%s (%s)",
                expectedPost.id(), Endpoint.POSTS.byId().replace("id", String.valueOf(expectedPost.id())));

        ApiResponse<PostResponse> postByExistingIdResponse = postClient.getById(expectedPost.id());

        assertThat(postByExistingIdResponse.statusCode())
                .as("Status code %s", HttpStatus.SC_OK)
                .isEqualTo(HttpStatus.SC_OK);
        assertSoftly(softly -> {
            softly.assertThat(postByExistingIdResponse.body().userId()).isEqualTo(expectedPost.userId());
            softly.assertThat(postByExistingIdResponse.body().id()).isEqualTo(expectedPost.id());
            softly.assertThat(postByExistingIdResponse.body().title()).isNotBlank();
            softly.assertThat(postByExistingIdResponse.body().body()).isNotBlank();
        });

        int badId = getTestData().badPostGetRequestId();
        getLogger().info("Step 3. Send GET request to get post with id=%s (%s)",
                badId, Endpoint.POSTS.byId().replace("id", String.valueOf(badId)));

        ApiResponse<Map<String, Object>> postByMissingIdResponse = postClient.getRawById(badId);

        assertThat(postByMissingIdResponse.statusCode())
                .as("Status code %s", HttpStatus.SC_NOT_FOUND)
                .isEqualTo(HttpStatus.SC_NOT_FOUND);
        assertThat(postByMissingIdResponse.body())
                .as("Response body is empty JSON object")
                .isEmpty();

        CreatePostRequest createRequest = getTestData().creatingPostRequest();
        getLogger().info("Step 4. Send POST request to create message with userId=%s and random title and body (%s)",
                createRequest.userId(), Endpoint.POSTS.getPath());

        ApiResponse<PostResponse> createdPostResponse = postClient.create(createRequest);

        assertThat(createdPostResponse.statusCode())
                .as("Status code %s", HttpStatus.SC_CREATED)
                .isEqualTo(HttpStatus.SC_CREATED);
        assertSoftly(softly -> {
            softly.assertThat(createdPostResponse.body().userId()).isEqualTo(createRequest.userId());
            softly.assertThat(createdPostResponse.body().title()).isEqualTo(createRequest.title());
            softly.assertThat(createdPostResponse.body().body()).isEqualTo(createRequest.body());
            softly.assertThat(createdPostResponse.body().id())
                    .as("Generated id")
                    .isPositive();
        });

        UserResponse expectedUser = getTestData().expectedUserGetRequest();
        getLogger().info("Step 5. Send GET request to get users (%s)", Endpoint.USERS.getPath());

        ApiResponse<List<UserResponse>> usersResponse = userClient.getAll();

        assertThat(usersResponse.statusCode())
                .as("Status code %s", HttpStatus.SC_OK)
                .isEqualTo(HttpStatus.SC_OK);
        assertThat(usersResponse.contentType())
                .as("Response body is json")
                .contains(ContentType.JSON.getValue());

        UserResponse actualUser = usersResponse.body().stream()
                .filter(u -> u.id() == expectedUser.id())
                .findFirst()
                .orElseThrow(() -> new AssertionError(String.format(
                        "User with id=%s not found in response", expectedUser.id())));

        assertThat(actualUser)
                .as("User id=%s from GET /users", expectedUser.id())
                .usingRecursiveComparison()
                .isEqualTo(expectedUser);

        getLogger().info("Step 6. Send GET request to get user with id=%s (%s)",
                expectedUser.id(), Endpoint.USERS.byId().replace("id", String.valueOf(expectedUser.id())));

        ApiResponse<UserResponse> userByIdResponse = userClient.getById(expectedUser.id());

        assertThat(userByIdResponse.statusCode())
                .as("Status Code %s", HttpStatus.SC_OK)
                .isEqualTo(HttpStatus.SC_OK);
        assertThat(userByIdResponse.body())
                .as("User from GET /users/%s matches user from GET /users", expectedUser.id())
                .usingRecursiveComparison()
                .isEqualTo(actualUser);
    }
}