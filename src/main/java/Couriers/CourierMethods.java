package Couriers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static Couriers.Constants.*;

public class CourierMethods {
    private Gson gson;

    public CourierMethods(){
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    //Удаление курьера
    public void deleteCourier(int courierId) {
        RestAssured
                .given()
                .header("Content-Type", "application/json")
                .delete(CREATE_ENDPOINT + courierId)
                .then()
                .statusCode(200); // Ожидаем успешное удаление курьера
    }

    //Получение ID курьера
    public int getCourierId(String login, String password) {
        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body("{ \"login\": \"" + login + "\", \"password\": \"" + password + "\" }")
                .post(CREATE_ENDPOINT);

        if (response.getStatusCode() == 200) {
            return response.jsonPath().getInt("id");
        } else {
            return -1; // Если авторизация не удалась
        }
    }
    //Форматируем тело ответа
    public String formatResponseBody(String responseBody) {
        JsonElement jsonElement = JsonParser.parseString(responseBody);
        return gson.toJson(jsonElement);
    }

    //Проверяем код ответа
    public void checkStatusCode(Response response, int expectedStatusCode) {
        assertThat(response.getStatusCode(), is(expectedStatusCode));
    }

    //Проверяем сообщение об ошибке
    public void checkErrorMessage(Response response, String expectedMessage) {
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
    }
    //Авторизация
    public int authorizeCourier(String login, String password) {
        int courierId = getCourierId(login, password);
        assertThat(courierId, is(not(-1))); // Убедитесь, что ID корректен
        return courierId;
    }

    //Вывести тело и код ответа
    public void printResponse(Response response, Gson gson) {
        String responseBody = response.getBody().asString();

        // Форматируем JSON для вывода
        String formattedJson = gson.toJson(gson.fromJson(responseBody, Object.class));

        // Выводим код ответа и форматированное тело ответа
        System.out.println("Код ответа: " + response.getStatusCode());
        System.out.println("Тело ответа: " + formattedJson);
    }

    // Метод создания тела запроса
    public String createRequestBody(String login, String password, String firstName) {
        return "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\", \"firstName\": \"" + firstName + "\" }";
    }

    // Метод отправки POST запроса на создание курьера
    public Response createCourier(String body) {
        return RestAssured.
                given()
                .header("Content-Type", "application/json")
                .body(body)
                .post(CREATE_ENDPOINT);
    }
}
