package couriers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static couriers.CourierConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Яндекс.Самокат")
@Feature("Тестирование API авторизации курьера")
@DisplayName("Проверка возможности авторизации курьера")
public class CourierAuthorizationError {

    private Gson gson;
    private int courierID = -1;
    private CourierMethods courierMethods = new CourierMethods();

    @Before
    @Step("Подготовка данных")
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        gson = new GsonBuilder().setPrettyPrinting().create();

    }

    @Test
    @DisplayName("Авторизация курьера без поля login")
    @Description("Появление сообщения об ошибке, если не указано поле login. Код и статус ответа 400 Bad Request.")
    @Severity(SeverityLevel.NORMAL)
    public void courierAuthorizationWithoutLoginTest() {
        // Создаем курьера
        Courier courier = new Courier("Davis", "1234", "McClean");
        String body = gson.toJson(courier);
        courierMethods.createCourier(body);

        // Задаем ожидаемое сообщение об ошибке
        String expectedErrorMessage = "Недостаточно данных для входа";

        // Создаем запрос для авторизации без поля password
        String bodyWithoutLogin = "{\"password\": \"1234\" }";

        // Получаем ID курьера
        courierID = courierMethods.getCourierId(courier.getLogin(), courier.getPassword());

        try {
            // Отправляем POST запрос для авторизации
            Response responseWithoutLogin = RestAssured
                    .given()
                    .header("Content-Type", "application/json")
                    .body(bodyWithoutLogin)
                    .config(RestAssured.config()
                            .httpClient(HttpClientConfig.httpClientConfig()
                                    .setParam("http.connection.timeout", 5000) // время на соединение
                                    .setParam("http.socket.timeout", 5000))) // таймаут ожидания ответа
                    .post(COURIER_LOGIN_ENDPOINT);

            // Проверяем код и статус ответа
            courierMethods.checkStatusCode(responseWithoutLogin, 400);
            courierMethods.printResponse(responseWithoutLogin, gson);
            assertThat(responseWithoutLogin.jsonPath().getString("message"), is(expectedErrorMessage));

        } finally {
            courierMethods.deleteCourier(courierID);
        }
    }

    @Test
    @DisplayName("Авторизация курьера без поля password")
    @Description("Появление сообщения об ошибке, если не указано поле password. Код и статус ответа 400 Bad Request.")
    @Severity(SeverityLevel.NORMAL)
    public void courierAuthorizationWithoutPasswordTest() {
        // Создаем курьера
        Courier courier = new Courier("Davis", "1234", "McClean");
        String body = gson.toJson(courier);
        courierMethods.createCourier(body);

        // Получаем ID курьера
        courierID = courierMethods.getCourierId(courier.getLogin(), courier.getPassword());

        try {
            // Создаем запрос для авторизации без поля password
            String bodyWithoutPassword = "{\"login\": \"Davis\" }";

            // Отправляем POST запрос для авторизации
            Response responseWithoutPassword = RestAssured
                    .given()
                    .header("Content-Type", "application/json")
                    .body(bodyWithoutPassword)
                    .config(RestAssured.config()
                            .httpClient(HttpClientConfig.httpClientConfig()
                                    .setParam("http.connection.timeout", 5000) // время на соединение
                                    .setParam("http.socket.timeout", 5000))) // таймаут ожидания ответа
                    .post(COURIER_LOGIN_ENDPOINT);

            // Задаем ожидаемое сообщение об ошибке
            String expectedErrorMessage = "Недостаточно данных для входа";

            // Проверяем код и статус ответа
            assertThat(responseWithoutPassword.getStatusCode(), is(400));
            assertThat(responseWithoutPassword.jsonPath().getString("message"), is(expectedErrorMessage));
        } finally {
            courierMethods.deleteCourier(courierID);
        }
    }

    @Test
    @DisplayName("Авторизация курьера без поля password и поля login")
    @Description("Появление сообщения об ошибке, если не указано поле password и поле login. Код и статус ответа 400 Bad Request.")
    @Severity(SeverityLevel.NORMAL)
    public void courierAuthorizationWithoutFieldsTest() {
        // Создаем курьера
        Courier courier = new Courier("Davis", "1234", "McClean");
        String body = gson.toJson(courier);
        courierMethods.createCourier(body);

        // Получаем ID курьера
        courierID = courierMethods.getCourierId(courier.getLogin(), courier.getPassword());

        try {
            // Создаем запрос для авторизации без логина и пароля
            String bodyWithoutFields = "{}";
            // Отправляем POST запрос для авторизации
            Response responseWithoutFields = RestAssured
                    .given()
                    .header("Content-Type", "application/json")
                    .body(bodyWithoutFields)
                    .config(RestAssured.config()
                            .httpClient(HttpClientConfig.httpClientConfig()
                                    .setParam("http.connection.timeout", 5000) // время на соединение
                                    .setParam("http.socket.timeout", 5000))) // таймаут ожидания ответа
                    .post(COURIER_LOGIN_ENDPOINT);
            // Задаем ожидаемое сообщение об ошибке
            String expectedErrorMessage = "Недостаточно данных для входа";

            // Проверяем код и статус ответа
            courierMethods.checkStatusCode(responseWithoutFields, 400);
            courierMethods.printResponse(responseWithoutFields, gson);
            assertThat(responseWithoutFields.jsonPath().getString("message"), is(expectedErrorMessage));

        } finally {
            courierMethods.deleteCourier(courierID);
        }
    }

    @Test
    @DisplayName("Авторизация с некорректными данными")
    @Description("Появление сообщения об ошибке, если указаны несуществующие логин и пароль. Код и статус ответа 404 Not Found.")
    @Severity(SeverityLevel.NORMAL)
    public void incorrectCourierAuthorizationTest() {
        String incorrectBody = "{ \"login\": \"Adamoff\", \"password\": \"12347\" }";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(incorrectBody)
                .post("/api/v1/courier/login");

        assertThat(response.getStatusCode(), is(404));
        assertThat(response.jsonPath().getString("message"), is("Учетная запись не найдена"));

        System.out.println("Not existent user login:");
        System.out.println("Response Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.asString());
    }
}
