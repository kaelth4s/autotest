package ru.kaelth4s.tests.dto;

import ru.kaelth4s.framework.util.RandomUtil;
import ru.kaelth4s.tests.dto.api.posts.CreatePostRequest;
import ru.kaelth4s.tests.dto.api.posts.PostResponse;
import ru.kaelth4s.tests.dto.api.users.UserResponse;

public record TestData(
        PostResponse expectedPostGetRequest,
        int badPostGetRequestId,
        CreatePostRequest creatingPostRequest,
        UserResponse expectedUserGetRequest
) {
    public static TestData from(TestData json) {
        CreatePostRequest creatingPostRequest = new CreatePostRequest(
                json.creatingPostRequest().userId(),
                RandomUtil.randomTitle(),
                RandomUtil.randomBody()

        );
        return new TestData(
                json.expectedPostGetRequest(),
                json.badPostGetRequestId(),
                creatingPostRequest,
                json.expectedUserGetRequest()
        );
    }
}