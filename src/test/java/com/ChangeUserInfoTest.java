package com;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.UserCredentials.*;
import static org.junit.Assert.*;

public class ChangeUserInfoTest {

    int success200StatusCode = 200;

    private User user;
    private UserClient userClient;
    private String accessToken;
    private String bearerToken;



    @Before
    public void setUp() {
        user = User.getRandom();
        userClient = new UserClient();
        accessToken = userClient.create(user).extract().path("accessToken");
        accessToken = accessToken.substring(7);
    }

    @After
    public void tearDown() {
        userClient.delete(accessToken);
    }

    @Test
    @DisplayName("Проверяет, что можно изменить EMAIL пользователя")
    public void checkUserEmailCanBeEdited() {
        int expectedStatusCode = 200;
        ValidatableResponse response = userClient.userInfoChange(accessToken, user.setEmail(getUserEmail()));

        int actualStatusCode = response.extract().statusCode();
        assertEquals("Ожидаемый статус код " + expectedStatusCode + ". Фактический " + actualStatusCode,
                expectedStatusCode, actualStatusCode);

        boolean isUserEmailEdited = response.extract().path("success");
        assertTrue("Email пользователя не обновился", isUserEmailEdited);

        String expectedUserEmail = user.getEmail();
        String actualUserEmail = response.extract().path("user.email");
        assertEquals("Ожидаемый email " + expectedUserEmail + ". Фактический " + actualUserEmail,
                expectedUserEmail, actualUserEmail);
    }

    @Test
    @DisplayName("Проверяет, что можно изменить PASSWORD пользователя")
    public void checkUserPasswordCanBeEdited() {
        int expectedStatusCode = 200;
        ValidatableResponse response = userClient.userInfoChange(accessToken, user.setPassword(getUserPassword()));

        int actualStatusCode = response.extract().statusCode();
        assertEquals("Ожиданемый статус код " + expectedStatusCode + ". Фактический " + actualStatusCode,
                expectedStatusCode, actualStatusCode);

        boolean isUserPasswordUpdated = response.extract().path("success");
        assertTrue("Пароль пользователя не обновился", isUserPasswordUpdated);
    }

    @Test
    @DisplayName("Проверяет, что можно изменить NAME пользователя")
    public void checkUserNameCanBeEdited() {
        int expectedStatusCode = 200;
        ValidatableResponse response = userClient.userInfoChange(accessToken, user.setName(getUserName()));

        int actualStatusCode = response.extract().statusCode();
        assertEquals("Ожидаемый статус код " + expectedStatusCode + ". Фактический " + actualStatusCode,
                expectedStatusCode, actualStatusCode);

        boolean isUserNameEdited = response.extract().path("success");
        assertTrue("Имя пользователя не обновилось", isUserNameEdited);

        String expectedName = user.getName();
        String actualName = response.extract().path("user.name");
        assertEquals("Ожидаемое имя " + expectedName + ". Фактическое " + actualName, expectedName, actualName);
    }

    @Test
    @DisplayName("Меняет сразу все данные пользователя: name, email, password")
    public void editingAllUserData() {
        userClient.create(user);
        ValidatableResponse login = userClient.login(UserCredentials.from(user));
        bearerToken = login.extract().path("accessToken");

        ValidatableResponse editedUser = userClient.editInfo(UserCredentials.getUserWithPasswordEmailAndName(user),
                bearerToken);

        int actualCode = editedUser.extract().statusCode();
        boolean isSuccessTrue = editedUser.extract().path("success");

        assertEquals("Ожидаемый статус код " + success200StatusCode + ". Фактический " + actualCode,
                success200StatusCode, actualCode);
        assertTrue("Должно вернуться true, но возвращается false", isSuccessTrue);
    }

    @Test
    @Description("Проверяет, что неавторизованный пользователь не может менять информацию о себе ")
    public void userInfoCanNotBeChangedWithoutAuthorizationNegativeTest() {
        String expectedErrorMessage = "You should be authorised";
        int expectedStatusCode = 401;

        userClient.create(user);
        ValidatableResponse info = userClient.editInfoWithoutToken(user);

        int actualStatusCode = info.extract().statusCode();
        boolean getUserInfo = info.extract().path("success");
        String actualErrorMessage = info.extract().path("message");

        assertEquals("Ожидаемый статус код " + expectedStatusCode + ". Фактический " + actualStatusCode,
                expectedStatusCode, actualStatusCode);
        assertFalse("Ожидаемый ответ false, по факту true", getUserInfo);
        assertEquals("Ожидаемое сообщение об ошибке " + expectedErrorMessage + ". Фактическое " + actualErrorMessage,
                expectedErrorMessage, actualErrorMessage);
    }
}
