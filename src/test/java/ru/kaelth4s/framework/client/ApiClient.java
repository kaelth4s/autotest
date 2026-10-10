package ru.kaelth4s.framework.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public abstract class ApiClient {
    protected Response get(RequestSpecification spec, String path) {
        return given().spec(spec).get(path);
    }

    protected Response get(RequestSpecification spec, String path, Map<String, ?> pathParams) {
        return given().spec(spec).pathParams(pathParams).get(path);
    }

    protected Response post(RequestSpecification spec, String path, Object body) {
        return given().spec(spec).body(body).post(path);
    }
}