package Couriers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static Couriers.Constants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("API. Курьеры")
@Feature("Создание курьера")
public class CourierCreation {

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
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Курьера можно создать")
    public void creationCourierTest() {
        // Создаем объект курьера
        Courier courier = new Courier("Tariq", "1234", "StPatric");

        // Преобразуем объект курьера в JSON
        String body = gson.toJson(courier);

        // Выполняем POST запрос для создания курьера
        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(body)
                .post(CREATE_ENDPOINT);

        // Проверяем статус код и ответ
        courierMethods.checkStatusCode(response, 201);
        assertThat(response.jsonPath().get("ok"), is(true));

        // Получаем ID курьера
        courierID = courierMethods.getCourierId(courier.getLogin(), courier.getPassword());
        if(courierID != -1) {
            System.out.println("Учетная запись курьера успешно создана в системе");
        }
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Успешный запрос возвращает ok: true")
    public void successfulCreationResponseBodyTest() {
        // Создаем объект курьера
        Courier courier = new Courier("James", "1234", "StPatric");

        // Преобразуем объект курьера в JSON
        String body = gson.toJson(courier);

        // Выполняем POST запрос для создания курьера
        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(body)
                .post(CREATE_ENDPOINT);

        //Форматируем тело ответа
        String formattedResponseBody = courierMethods.formatResponseBody(response.getBody().asString());

        // Проверяем, что ответ содержит "ok: true"
        assertThat(formattedResponseBody, containsString("\"ok\": true"));
        System.out.println("Возвращается тело ответа ok:true");

        // Получаем ID курьера
        courierID = courierMethods.getCourierId(courier.getLogin(), courier.getPassword());
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Успешный запрос возвращает код ответа 201 Created")
    public void successfulCreationStatusCode201Test() {
        // Создаем объект курьера
        Courier courier = new Courier("Tasha", "1234", "StPatric");

        // Преобразуем объект курьера в JSON
        String body = gson.toJson(courier);

        // Выполняем POST запрос для создания курьера
        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(body)
                .post(CREATE_ENDPOINT);

        // Проверяем статус код и ответ
        courierMethods.checkStatusCode(response, 201);
        System.out.println("Код ответа 201 Created");

        // Получаем ID курьера
        courierID = courierMethods.getCourierId(courier.getLogin(), courier.getPassword());
    }
}

