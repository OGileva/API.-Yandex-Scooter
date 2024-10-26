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
    @Description("При создании курьеров с одинаковым логином запрос возвращает сообщение об ошибке")
    public void creationSameCouriersTest() {
        // Создаем объект курьера
        Courier courier = new Courier("qazhof", "1234", "saske");
        // Преобразуем объект курьера в JSON
        String body = gson.toJson(courier);


        Response firstCourierResponse = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(body)
                .post(CREATE_ENDPOINT);

        Response secondCourierResponse = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(body)
                .post(CREATE_ENDPOINT);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        courierMethods.printResponse(secondCourierResponse, gson);
        assertThat(secondCourierResponse.getStatusCode(), is(409));
        String expectedMessage = "Этот логин уже используется. Попробуйте другой.";
        assertThat(secondCourierResponse.jsonPath().getString("message"), is(expectedMessage));
        courierID = courierMethods.getCourierId(courier.getLogin(), courier.getPassword());
        assertThat(courierID, is(not(-1)));
    }
}