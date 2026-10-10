package ru.kaelth4s.api.dto;

import ru.kaelth4s.api.dto.posts.CreatePostRequest;
import ru.kaelth4s.api.dto.posts.PostResponse;
import ru.kaelth4s.api.dto.users.UserResponse;
import ru.kaelth4s.framework.util.RandomUtil;

public record ApiTestData(
        PostResponse expectedPostGetRequest,
        int badPostGetRequestId,
        CreatePostRequest creatingPostRequest,
        UserResponse expectedUserGetRequest
) {
    public static ApiTestData from(ApiTestData json) {
        CreatePostRequest creatingPostRequest = new CreatePostRequest(
                json.creatingPostRequest().userId(),
                RandomUtil.randomTitle(),
                RandomUtil.randomBody()

        );
        return new ApiTestData(
                json.expectedPostGetRequest(),
                json.badPostGetRequestId(),
                creatingPostRequest,
                json.expectedUserGetRequest()
        );
    }
}