package com;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RestAssuredClient {

    public static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api/";

    public RequestSpecification getBaseSpec() {
        return new RequestSpecBuilder()
                .addFilter(new AllureRestAssured())
                .setContentType(ContentType.JSON)
                .setBaseUri(BASE_URL)
                .build();
    }
}