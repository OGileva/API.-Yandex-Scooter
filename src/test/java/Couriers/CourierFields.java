package Couriers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static Couriers.Constants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@Epic("API. Курьеры")
@Feature("Создание курьера")
public class CourierFields {

    private Gson gson;
    private int courierID = -1;
    private CourierMethods courierMethods = new CourierMethods();

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @After
    public void tearDown() {
        if (courierID != -1) {
            courierMethods.deleteCourier(courierID);
        }
    }
    @Test
    @Story("Проверка обязательных полей для создания курьера")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Успешное создание курьера с полем логин и пароль")
    public void testCreateCourierWithAllRequiredFields() {
        // Создаем объект курьера
        Courier courier = new Courier("qazhof", "1234", "saske");
        // Преобразуем объект курьера в JSON
        String body = gson.toJson(courier);
        // Отправляем POST запрос
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post(CREATE_ENDPOINT);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        courierMethods.printResponse(response, gson); // Вызов метода
        assertThat(response.getStatusCode(), is(201));
        assertThat(response.jsonPath().get("ok"), is(true));
        courierID = courierMethods.getCourierId(courier.getLogin(), courier.getPassword()); // Сохраняем ID курьера
        assertThat(courierID, is(not(-1))); // Убедитесь, что ID не -1
    }

    @Test
    @Story("Появление сообщения об ошибке, если не заполнены обязательные поля")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Запрос возвращает ошибку если не указано поле логин")
    public void testCreateCourierWithoutLogin() {
        String bodyWithoutLogin = "{ \"password\": \"1234\", \"firstName\": \"saske\" }";
        String expectedMessage = "Недостаточно данных для создания учетной записи";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(bodyWithoutLogin)
                .when()
                .post("/api/v1/courier");
        courierMethods.printResponse(response, gson);
        assertThat(response.getStatusCode(), is(400));
        System.out.println("Курьер не создан: пропущено поле login");
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
    }

    @Test
    @Story("Появление сообщения об ошибке, если не заполнены обязательные поля")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Запрос возвращает ошибку если не указано поле пароль")
    public void testCreateCourierWithoutPassword() {
        String login = "qazhof" + System.currentTimeMillis();
        String bodyWithoutPassword = "{ \"login\": \"" + login + "\", \"firstName\": \"saske\" }";
        String expectedMessage = "Недостаточно данных для создания учетной записи";
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(bodyWithoutPassword)
                .when()
                .post("/api/v1/courier");
        courierMethods.printResponse(response, gson);
        assertThat(response.getStatusCode(), is(400));
        System.out.println("Курьер не создан: пропущено поле password");
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
    }
}