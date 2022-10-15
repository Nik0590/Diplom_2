package com;

import Clients.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.UserCredentials.*;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ChangeUserInfoTest {

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
        ValidatableResponse response = userClient.userInfoChange(accessToken, user.setEmail(getUserEmail()));

        int actualStatusCode = response.extract().statusCode();
        assertThat("Ожидаемый статус код " + SC_OK + ". Фактический " + actualStatusCode,
                actualStatusCode, equalTo(SC_OK));

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
        ValidatableResponse response = userClient.userInfoChange(accessToken, user.setPassword(getUserPassword()));

        int actualStatusCode = response.extract().statusCode();
        assertThat("Ожиданемый статус код " + SC_OK + ". Фактический " + actualStatusCode,
                actualStatusCode, equalTo(SC_OK));

        boolean isUserPasswordUpdated = response.extract().path("success");
        assertTrue("Пароль пользователя не обновился", isUserPasswordUpdated);
    }

    @Test
    @DisplayName("Проверяет, что можно изменить NAME пользователя")
    public void checkUserNameCanBeEdited() {
        ValidatableResponse response = userClient.userInfoChange(accessToken, user.setName(getUserName()));

        int actualStatusCode = response.extract().statusCode();
        assertThat("Ожидаемый статус код " + SC_OK + ". Фактический " + actualStatusCode,
                actualStatusCode, equalTo(SC_OK));

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

        int actualStatusCode = editedUser.extract().statusCode();
        boolean isSuccessTrue = editedUser.extract().path("success");

        assertThat("Ожидаемый статус код " + SC_OK + ". Фактический " + actualStatusCode,
                actualStatusCode, equalTo(SC_OK));
        assertTrue("Должно вернуться true, но возвращается false", isSuccessTrue);
    }

    @Test
    @Description("Проверяет, что неавторизованный пользователь не может менять информацию о себе ")
    public void userInfoCanNotBeChangedWithoutAuthorizationNegativeTest() {
        String expectedErrorMessage = "You should be authorised";
        userClient.create(user);
        ValidatableResponse info = userClient.editInfoWithoutToken(user);

        int actualStatusCode = info.extract().statusCode();
        boolean getUserInfo = info.extract().path("success");
        String actualErrorMessage = info.extract().path("message");

        assertThat("Ожидаемый статус код " + SC_UNAUTHORIZED + ". Фактический " + actualStatusCode,
                actualStatusCode, equalTo(SC_UNAUTHORIZED));
        assertFalse("Ожидаемый ответ false, по факту true", getUserInfo);
        assertEquals("Ожидаемое сообщение об ошибке " + expectedErrorMessage + ". Фактическое " + actualErrorMessage,
                expectedErrorMessage, actualErrorMessage);
    }
}
