package com;

import Clients.UserClient;
import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class CreateUserTest {

    private User user;
    private UserClient userClient;
    private String accessToken;
    String bearerToken;

    // Создание рандомного пользователя
    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.getRandom();
    }

    @After
    public void tearDown() {
        accessToken = accessToken.substring(7);
        userClient.delete(accessToken);
    }

    @Test
    @Description("Проверка регистрации пользователя с валидными данными")
    public void userCanBeCreatedTest() {
        // Создание пользователя
        ValidatableResponse response = userClient.create(user);
        accessToken = response.extract().path("accessToken");
        // Получение статус кода с тела создания пользователя
        int actualStatusCode = response.extract().statusCode();
        // Получение тела ответа при создании пользователя
        boolean isUserCreated = response.extract().path("success");
        // Получение токена пользователя
        bearerToken = response.extract().path("accessToken");
        // Проверка что статус код соответсвует ожиданиям
        assertThat("Expected status code is " + SC_OK + ". But actual is " + actualStatusCode,
                actualStatusCode, equalTo(SC_OK));
        // Проверка что пользователь создался
        assertTrue("User is not created", isUserCreated);
        // Проверка что токен пользователя не пустой
        assertThat("User access token is has null value", bearerToken, notNullValue());
    }

    @Test
    @Description("Проверка что нельзя зарегистрировать 2х одинаковых пользователей")
    public void checkThatItImpossibleRegister2IdenticalUsersTest() {
        String expectedErrorMessage = "User already exists";
        // Создание клиента
        accessToken = userClient.create(user).extract().path("accessToken");
        // Попытка создания пользователя с теми же данными
        ValidatableResponse response = userClient.create(user);
        // Получение статус кода с тела создания одинакового пользователя
        int actualStatusCode = response.extract().statusCode();
        // Получение тела ответа при создании одинакового пользователя
        boolean checkUserWasNotCreated = response.extract().path("success");
        // Получение тела сообщения
        String actualErrorMessage = response.extract().path("message");
        // Проверка что статус код соответсвует ожидаемому
        assertEquals("Expected status code is " + SC_FORBIDDEN + ". But actual is " + actualStatusCode,
                actualStatusCode, equalTo(SC_FORBIDDEN));
        // Проверка что одинаковый пользователь не создался
        assertFalse("User is created", checkUserWasNotCreated);
        // Проверка что сообщение об ошибке соответсвует ожидаемому
        assertEquals("Expected error massage is '" + expectedErrorMessage + "'. But actual is '" + actualErrorMessage + "'.",
                expectedErrorMessage, actualErrorMessage);
    }
}