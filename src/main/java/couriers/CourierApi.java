package couriers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.response.Response;

import static couriers.CourierConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CourierApi {
    private Gson gson;

    public CourierApi() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    //Удаление курьера
    @Step("Удаление курьера")
    public Response deleteCourier(int courierId) {
        return RestAssured
                .given()
                .header("Content-Type", "application/json")
                .delete(COURIER_DELETE_ENDPOINT + courierId)
                .then()
                .extract()
                .response();
    }


    //Получение ID курьера
    @Step("Получение ID курьера")
    public int getCourierId(String login, String password) {

        Courier courier = new Courier(login, password);
        Gson gson = new Gson();
        String jsonBody = gson.toJson(courier);

        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(jsonBody)
                .post(COURIER_LOGIN_ENDPOINT);
            return response.jsonPath().getInt("id");
    }

    //Форматируем тело ответа
    @Step("Получение тела ответа в формате JSON")
    public String formatResponseBody(String responseBody) {
        JsonElement jsonElement = JsonParser.parseString(responseBody);
        return gson.toJson(jsonElement);
    }

    //Проверяем код ответа
    @Step("Проверка кода ответа")
    public void checkStatusCode(Response response, int expectedStatusCode) {
        assertThat(response.getStatusCode(), is(expectedStatusCode));
    }

    //Вывести тело и код ответа
    @Step("Получение тела и кода ответа")
    public void printResponse(Response response, Gson gson) {
        String responseBody = response.getBody().asString();

        // Форматируем JSON для вывода
        String formattedJson = gson.toJson(gson.fromJson(responseBody, Object.class));

        // Выводим код ответа и форматированное тело ответа
        System.out.println("Код ответа: " + response.getStatusCode());
        System.out.println("Тело ответа: " + formattedJson);
    }

    // Метод отправки POST запроса на создание курьера
    @Step("Отправка POST запроса на создание курьера")
    public Response createCourier(String body) {
        return RestAssured.
                given()
                .header("Content-Type", "application/json")
                .body(body)
                .post(COURIER_CREATE_ENDPOINT);
    }

    //Отправка POST запроса для авторизации
    @Step("Авторизация курьера")
    public Response authorizeCourier(String requestBody) {
        return RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .config(RestAssured.config()
                        .httpClient(HttpClientConfig.httpClientConfig()
                                .setParam("http.connection.timeout", 5000) // время на соединение
                                .setParam("http.socket.timeout", 5000))) // таймаут ожидания ответа
                .post(COURIER_LOGIN_ENDPOINT);
    }

}