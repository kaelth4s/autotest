package ru.kaelth4s.framework.api.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public abstract class ApiClient {
    protected Response postWithQuery(RequestSpecification spec, String path, Map<String, ?> queryParams) {
        return given().spec(spec).queryParams(queryParams).post(path);
    }
}