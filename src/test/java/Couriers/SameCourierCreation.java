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
import static org.hamcrest.Matchers.is;

@Epic("API. Курьеры")
@Feature("Создание курьера")
public class SameCourierCreation {

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
    @Story("Создание курьеров с одинаковым логином")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void creationSameCouriersTest() {
        // Создаем объект курьера
        Courier courier = new Courier("ророо", "1234", "Moralez");

        // Преобразуем объект курьера в JSON
        String body = gson.toJson(courier);

        //Создаем первого курьера
        Response firstCourierResponse = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(body)
                .post(CREATE_ENDPOINT);

        //Создаем второго курьера с таким же логином
        Response secondCourierResponse = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(body)
                .post(CREATE_ENDPOINT);

        // Проверяем статус ответа
        courierMethods.checkStatusCode(secondCourierResponse, 409);
        System.out.println("Вы создаете курьера с уже существующим логином");

        courierID = courierMethods.getCourierId(courier.getLogin(), courier.getPassword());
    }

    @Test
    @Story("Создание курьеров с одинаковым логином")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Если создать пользователя с логином, который уже есть, возвращается ошибка")
    public void sameCourierCreationResponseErrorTest() {
        // Создаем объект курьера
        Courier courier = new Courier("ророо", "1234", "Moralez");

        // Преобразуем объект курьера в JSON
        String body = gson.toJson(courier);

        //Создаем первого курьера
        Response firstCourierResponse = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(body)
                .post(CREATE_ENDPOINT);

        //Создаем второго курьера с таким же логином
        Response secondCourierResponse = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(body)
                .post(CREATE_ENDPOINT);

        //Проверяем сообщение об ошибке
        courierMethods.printResponse(secondCourierResponse, gson);
        String expectedMessage = "Этот логин уже используется. Попробуйте другой.";
        assertThat(secondCourierResponse.jsonPath().getString("message"), is(expectedMessage));

        //Получаем ID курьера
        courierID = courierMethods.getCourierId(courier.getLogin(), courier.getPassword());
    }
}