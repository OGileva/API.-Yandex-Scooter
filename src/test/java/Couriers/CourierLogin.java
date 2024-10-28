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

import static Couriers.CourierConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("API. Курьеры")
@Feature("Авторизация курьера")
public class CourierLogin {

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
    @DisplayName("Курьер может авторизоваться")
    public void courierAuthorizationTest() {

        //Создаем курьера
        Courier courier = new Courier("Drew", "1234");
        String body = gson.toJson(courier);
        courierMethods.createCourier(body);

        //Создаем запрос для авторизации
        String loginBody = "{ \"login\": \"Drew\", \"password\": \"1234\" }";

        //Отправляем POST запрос для авторизации
        Response loginResponse = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(loginBody)
                .post(COURIER_LOGIN_ENDPOINT);

        //Проверяем код ответа
        courierMethods.checkStatusCode(loginResponse, 200);
        System.out.println("Курьер успешно авторизован");

        courierID = courierMethods.getCourierId(courier.getLogin(), courier.getPassword());
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("При успешной авторизации возвращается ID курьера")
    public void courierAuthorizationGetIdTest() {

        //Создаем курьера
        Courier courier = new Courier("Drew", "1234");
        String body = gson.toJson(courier);
        courierMethods.createCourier(body);

        //Создаем запрос для авторизации
        String loginBody = "{ \"login\": \"Kane\", \"password\": \"1234\" }";

        //Отправляем POST запрос для авторизации
        Response loginResponse = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(loginBody)
                .post(COURIER_LOGIN_ENDPOINT);

        //Получаем ID курьера
        courierID = courierMethods.getCourierId(courier.getLogin(), courier.getPassword());
        // Проверяем ID курьера получен корректно
        assertThat(courierID, is(not(-1)));
        System.out.println("Успешный запрос возвращает ID курьера " + loginResponse.asString());
    }
}
