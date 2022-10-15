package com;

import Clients.IngredientsClient;
import Clients.OrderClient;
import Clients.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CreateOrderTest {

    private UserClient userClient;
    private User user;
    private String accessToken;

    List<String> ingredients = new ArrayList<>();
    private int orderNumber;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.getRandom();
        accessToken = userClient.create(user).extract().path("accessToken");
        accessToken = accessToken.substring(7);
    }

    @After
    public void tearDown() {
        userClient.delete(accessToken);
    }

    @Test
    @DisplayName("Checking user can create order with ingredients after authorization")
    public void checkAuthUserCanMakeAnOrderTest() {
        ingredients = new IngredientsClient().getIngredients().extract().path("data._id");
        IngredientsHashes orderIngredients = new IngredientsHashes(ingredients.get(0));
        ValidatableResponse response = new OrderClient().createOrderWithToken(orderIngredients, accessToken);

        int actualStatusCode = response.extract().statusCode();
        boolean isOrderSuccessfullyCreated = response.extract().path("success");
        orderNumber = response.extract().path("order.number");

        assertThat("Ожидаемый статус код " + SC_OK + ". Фактический " + actualStatusCode,
                actualStatusCode, equalTo(SC_OK));
        assertTrue("Заказ не создан", isOrderSuccessfullyCreated);
        assertThat("Номер заказа пустой", orderNumber, notNullValue());
    }

    @Test
    @DisplayName("This test verifies that it is allowed to create order with ingredients without authorization")
    public void checkUserCanMakeAnOrderWithoutAuthorizationTest() {
        ingredients = new IngredientsClient().getIngredients().extract().path("data._id");
        IngredientsHashes orderIngredients = new IngredientsHashes(ingredients.get(0));
        ValidatableResponse response = new OrderClient().createOrderWithToken(orderIngredients, "");
        int actualStatusCode = response.extract().statusCode();
        boolean isOrderSuccessfullyCreated = response.extract().path("success");
        int orderNumber = response.extract().path("order.number");

        assertEquals("Ожидаемый статус код " + SC_OK + ". Фактический " + actualStatusCode,
                actualStatusCode, equalTo(SC_OK));
        assertTrue("Заказ не создаля. Должно вернуться true, возвращается false", isOrderSuccessfullyCreated);
        assertThat("Нет номера заказа", orderNumber, notNullValue());
    }

    @Test
    @DisplayName("This test verifies that it is not allowed to create an order with no ingredients")
    public void checkUserCanNotCreateAnOrderWithNoIngredientsTest() {
        IngredientsHashes ingredients = new IngredientsHashes(null);
        ValidatableResponse response = new OrderClient().createOrderWithToken(ingredients, accessToken);
        int actualStatusCode = response.extract().statusCode();
        boolean isOrderNotCreated = response.extract().path("message").equals("Ingredient ids must be provided");

        assertEquals("Ожидаемый статус код " + SC_BAD_REQUEST + ". Фактический " + actualStatusCode,
                actualStatusCode, equalTo(SC_BAD_REQUEST));
        assertTrue("Заказ создался с пустым списком ингредиентов. Заказ не должен быть создан", isOrderNotCreated);
    }

    @Test
    @DisplayName("This test verifies that it is not allowed to create an order with invalid id of ingredients")
    public void checkUserCanNotCreateAnOrderWithInvalidIngredientsIdTest() {
        IngredientsHashes ingredients = new IngredientsHashes("what is those?");
        ValidatableResponse response = new OrderClient().createOrderWithToken(ingredients, accessToken);

        int actualStatusCode = response.extract().statusCode();

        assertEquals("Ожидаемый статус код " + SC_INTERNAL_SERVER_ERROR + ". Фактический " + actualStatusCode,
                actualStatusCode, equalTo(SC_INTERNAL_SERVER_ERROR));
    }
}
