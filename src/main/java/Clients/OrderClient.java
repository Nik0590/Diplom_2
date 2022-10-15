package Clients;

import com.IngredientsHashes;
import com.User;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestAssuredClient {

    private static final String ORDER_PATH = "orders";
    private static final String CREATE_ORDER_BODY = "ingredients: [61c0c5a71d1f82001bdaaa6d, 61c0c5a71d1f82001bdaaa6f]";

    // получение заказа
    @DisplayName("Check if it's possible to create an order")
    @Description("Basic positive test that validate you are allowed to order scooter with any available color")
    @Step("Sends POST request to " + ORDER_PATH + " endpoint")
    public ValidatableResponse getUserOrders(User user) {
        return given()
                .spec(getBaseSpec())
                .body(CREATE_ORDER_BODY)
                .when()
                .get(ORDER_PATH)
                .then();
    }

    // создание заказа
    @DisplayName("Check if it's possible to create an order")
    @Description("Basic positive test that validate you are allowed to order scooter with any available color")
    @Step("Sends POST request to " + ORDER_PATH + " endpoint")
    public ValidatableResponse makeOrder(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(ORDER_PATH)
                .then();
    }

    @Step("Получить заказы конкретного пользователя c авторизацией")
    public ValidatableResponse getAuthorizedUserOrders(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .headers("Authorization", accessToken)
                .when()
                .get(BASE_URL + ORDER_PATH)
                .then();
    }

    @Step("Получить заказы конкретного пользователя без авторизации")
    public ValidatableResponse getUnauthorizedUserOrders() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(BASE_URL + ORDER_PATH)
                .then();
    }

    @Step("Создать заказ с токеном")
    public ValidatableResponse createOrderWithToken(IngredientsHashes ingredientsHashes, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .headers("Authorization", accessToken)
                .body(ingredientsHashes) // ingredients
                .when()
                .post(BASE_URL + ORDER_PATH)
                .then();
    }
}
