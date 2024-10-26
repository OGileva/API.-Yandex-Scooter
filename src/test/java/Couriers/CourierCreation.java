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
    @Story("Создание нового курьера")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Курьера возможно создать, запрос возвращает правильный код и тело ответа")
    public void creationCourierTest() {
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

        // Проверяем статус код и ответ
        courierMethods.checkStatusCode(response, 201);
        assertThat(response.jsonPath().get("ok"), is(true));

        // Получаем ID курьера
        courierID = courierMethods.getCourierId(courier.getLogin(), courier.getPassword());
        assertThat(courierID, is(not(-1)));
    }

    @Test
    @Story("Подтвердите «ok: true» для успешного создания курьера.")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Убедитесь, что успешное создание курьера возвращает в ответе «ok: true».")
    public void testCreateCourierOkTrue() {
        String body = courierMethods.createRequestBody("Tariq", "1234", "StPatric");
        Response response = courierMethods.createCourier(body);

        String formattedResponseBody = courierMethods.formatResponseBody(response.getBody().asString());

        System.out.println("Formatted response body: " + formattedResponseBody);
        // Авторизуемся и сохраняем ID курьера
        courierID = courierMethods.authorizeCourier("Tariq", "1234");
    }

}

