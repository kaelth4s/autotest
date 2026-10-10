package ru.kaelth4s.framework.spec;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import static aquality.selenium.browser.AqualityServices.getLogger;

public class AqualityLoggingFilter implements Filter {
    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {

        logRequest(requestSpec);
        Response response = ctx.next(requestSpec, responseSpec);
        logResponse(response);
        return response;
    }

    private void logRequest(FilterableRequestSpecification spec) {
        getLogger().info("Request: %s %s", spec.getMethod(), spec.getURI());

        if (spec.getHeaders() != null && spec.getHeaders().size() > 0) {
            getLogger().info("Request headers: %s", flatten(spec.getHeaders().toString()));
        }

        if (spec.getBody() != null) {
            getLogger().info("Request body: %s", flatten(spec.getBody().toString()));
        }
    }

    private void logResponse(Response response) {
        getLogger().info("Response: %s %s", response.statusCode(), response.statusLine());

        if (response.getHeaders() != null) {
            getLogger().info("Response headers: %s", flatten(response.getHeaders().toString()));
        }

        String body = response.asString();
        if (body != null && !body.isEmpty()) {
            getLogger().info("Response body: %s", flatten(body));
        }
    }

    private String flatten(String value) {
        return value.replaceAll("\\s+", " ")
                .replace("\\n", " ")
                .replace("\\r", "")
                .replace("\\t", " ")
                .trim();
    }
}