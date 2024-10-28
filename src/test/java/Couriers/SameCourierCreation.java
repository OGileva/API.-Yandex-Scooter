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
import static org.hamcrest.Matchers.is;

@Epic("Яндекс.Самокат")
@Feature("Тестирование API создания курьера")
@DisplayName("Проверка возможности создания курьера")
public class SameCourierCreation {

    private Gson gson;
    private int courierID = -1;
    private CourierMethods courierMethods = new CourierMethods();

    @Before
    @Step("Подготовка данных")
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @After
    @Step("Удаление курьера после теста")
    public void tearDown() {
        if (courierID != -1) {
            courierMethods.deleteCourier(courierID);
        }
    }

    @Test
    @DisplayName("Проверка возможности создания курьера с существующим логином логином")
    @Description("Нельзя создать курьера с существующим логином. Код и статус ответа 409 Сonflict.")
    @Severity(SeverityLevel.CRITICAL)
    public void creationSameCouriersTest() {
        // Создаем объект курьера
        Courier courier = new Courier("Brayden", "1234", "Westen");

        // Преобразуем объект курьера в JSON
        String body = gson.toJson(courier);

        //Создаем первого курьера
        Response firstCourierResponse = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(body)
                .post(COURIER_CREATE_ENDPOINT);

        //Создаем второго курьера с таким же логином
        Response secondCourierResponse = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(body)
                .post(COURIER_CREATE_ENDPOINT);

        // Проверяем статус ответа
        courierMethods.checkStatusCode(secondCourierResponse, 409);
        System.out.println("Вы создаете курьера с уже существующим логином");

        courierID = courierMethods.getCourierId(courier.getLogin(), courier.getPassword());
    }

    @Test
    @DisplayName("Проверка сообщения об ошибке при создании курьера с существующим логином")
    @Description("Появление сообщения об ошибке при создании курьера с существующим логином. Текст сообщения: Этот логин уже используется")
    @Severity(SeverityLevel.CRITICAL)
    public void sameCourierCreationResponseErrorTest() {
        // Создаем объект курьера
        Courier courier = new Courier("Effie", "1234", "Moralez");

        // Преобразуем объект курьера в JSON
        String body = gson.toJson(courier);

        //Создаем первого курьера
        Response firstCourierResponse = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(body)
                .post(COURIER_CREATE_ENDPOINT);

        //Создаем второго курьера с таким же логином
        Response secondCourierResponse = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(body)
                .post(COURIER_CREATE_ENDPOINT);

        //Проверяем сообщение об ошибке
        courierMethods.printResponse(secondCourierResponse, gson);
        String expectedMessage = "Этот логин уже используется. Попробуйте другой.";
        assertThat(secondCourierResponse.jsonPath().getString("message"), is(expectedMessage));

        //Получаем ID курьера
        courierID = courierMethods.getCourierId(courier.getLogin(), courier.getPassword());
    }
}