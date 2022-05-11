package com;

import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class UnsuccessfulCreatingUserWithInvalidDataNegativeTest {

    private final User user;
    private final int expectedStatus;
    private final String expectedErrorMessage;
    private static final String errorMessage = "Email, password and name are required fields";

    public UnsuccessfulCreatingUserWithInvalidDataNegativeTest(User user, int expectedStatus, String expectedErrorMessage) {
        this.user = user;
        this.expectedStatus = expectedStatus;
        this.expectedErrorMessage = expectedErrorMessage;
    }

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object[][]{
                {User.getUserWithEmailOnly(), 403, errorMessage},
                {User.getUserWithPasswordOnly(), 403, errorMessage},
                {User.getUserWithNameOnly(), 403, errorMessage},
                {User.getUserWithoutEmail(), 403, errorMessage},
                {User.getUserWithoutPassword(), 403, errorMessage},
                {User.getUserWithoutName(), 403, errorMessage}
        };
    }

    @Test
    @Description("Проверка что пользователя нельзя создать " +
            "1. Только с полем емаил " +
            "2. Только с полем пароль " +
            "3. Только с полем имя" +
            "4. Без поля емаил" +
            "5. Без поля пароль" +
            "6. Без поля имя")

    public void courierNotCreatedWithoutNecessaryField() {

        // Создание курьера
        ValidatableResponse response = new UserClient().create(user);
        // Получение статус кода запроса
        int statusCode = response.extract().statusCode();
        // Получение тела ответа при создании клиента
        boolean isUserNotCreated = response.extract().path("success");
        // Получение значения ключа "Message"
        String errorMessage = response.extract().path("message");

        // Проверка что статус код соответвует ожиданиям
        assertEquals("Status code is incorrect", expectedStatus, statusCode);
        // Проверка что курьер создался
        assertFalse("User is created", isUserNotCreated);
        // Проверка что сообщение об ошибке соответвует ожиданиям
        assertEquals("Error message is incorrect", expectedErrorMessage, errorMessage);
    }
}

