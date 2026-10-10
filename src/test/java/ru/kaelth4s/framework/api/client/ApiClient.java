package ru.kaelth4s.framework.api.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import ru.kaelth4s.tests.api.enums.ContentType;
import ru.kaelth4s.tests.api.enums.Header;

import java.util.Map;

import static io.restassured.RestAssured.given;

public abstract class ApiClient {

    static {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        RestAssured.config = RestAssured.config()
                .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                        .jackson2ObjectMapperFactory((type, s) -> mapper));
    }

    protected Response getWithQuery(RequestSpecification spec, String path, Map<String, ?> queryParams) {
        return given().spec(spec).queryParams(queryParams).get(path);
    }

    protected Response postWithQuery(RequestSpecification spec, String path, Map<String, ?> queryParams) {
        return given().spec(spec).queryParams(queryParams).post(path);
    }

    protected Response putBinary(String absoluteUrl, byte[] content) {
        return given()
                .header(Header.CONTENT_TYPE.getValue(), ContentType.OCTET.getValue())
                .body(content)
                .put(absoluteUrl);
    }

    protected Response putWithQuery(RequestSpecification spec, String path, Map<String, ?> queryParams) {
        return given().spec(spec).queryParams(queryParams).put(path);
    }

    protected Response deleteWithQuery(RequestSpecification spec, String path, Map<String, ?> queryParams) {
        return given().spec(spec).queryParams(queryParams).delete(path);
    }

    protected <T> ApiResponse<T> toApiResponse(Response response, Class<T> type) {
        return new ApiResponse<>(
                response.statusCode(),
                response.contentType(),
                response.as(type));
    }
}