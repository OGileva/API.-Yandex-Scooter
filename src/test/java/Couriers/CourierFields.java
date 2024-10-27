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
        Courier courier = new Courier("Monet", "1234", "Tejada");

        // Преобразуем объект курьера в JSON
        String body = gson.toJson(courier);

        // Отправляем POST запрос
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post(CREATE_ENDPOINT);

        // Проверяем статус код и ответ
        courierMethods.checkStatusCode(response, 201);
        assertThat(response.jsonPath().get("ok"), is(true));

        //Получаем ID курьера
        courierID = courierMethods.getCourierId(courier.getLogin(), courier.getPassword());
        System.out.println("Успешное создание учетной записи с полями login, password, first name");
    }

    @Test
    @Story("Появление сообщения об ошибке, если не заполнены обязательные поля")
    @Severity(SeverityLevel.NORMAL)
    @Description("Запрос возвращает ошибку если не указано поле логин")
    public void courierCreationWithoutLoginTest() {
        //Создаем тело запроса без логина
        String bodyWithoutLogin = "{ \"password\": \"1234\", \"firstName\": \"Tejada\" }";
        //Задаем ожидаемое сообщение об ошибке
        String expectedMessage = "Недостаточно данных для создания учетной записи";

        //Отправляем POST запрос на создание курьера
        Response response = RestAssured.
                given()
                .header("Content-Type", "application/json")
                .body(bodyWithoutLogin)
                .post(CREATE_ENDPOINT);

        courierMethods.printResponse(response, gson);
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
        System.out.println("Курьер не создан: не заполнено поле login");
    }

    @Test
    @Story("Появление сообщения об ошибке, если не заполнены обязательные поля")
    @Severity(SeverityLevel.NORMAL)
    @Description("Запрос возвращает ошибку если не указано поле пароль")
    public void courierCreationWithoutPasswordTest() {
        //Создаем тело запроса без пароля
        String bodyWithoutLogin = "{ \"login\": \"Diana\", \"firstName\": \"Tejada\" }";
        //Задаем ожидаемое сообщение об ошибке
        String expectedMessage = "Недостаточно данных для создания учетной записи";

        //Отправляем POST запрос на создание курьера
        Response response = RestAssured.
                given()
                .header("Content-Type", "application/json")
                .body(bodyWithoutLogin)
                .post(CREATE_ENDPOINT);

        courierMethods.printResponse(response, gson);
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
        System.out.println("Курьер не создан: не заполнено поле password");
    }

    @Test
    @Story("Появление сообщения об ошибке, если не заполнены обязательные поля")
    @Severity(SeverityLevel.NORMAL)
    @Description("Запрос возвращает ошибку если не указано поле пароль")
    public void courierCreationWithoutRequiredFieldsTest() {
        //Создаем тело запроса без логина и пароля
        String bodyWithoutLogin = "{\"firstName\": \"Tejada\" }";
        //Задаем ожидаемое сообщение об ошибке
        String expectedMessage = "Недостаточно данных для создания учетной записи";

        //Отправляем POST запрос на создание курьера
        Response response = RestAssured.
                given()
                .header("Content-Type", "application/json")
                .body(bodyWithoutLogin)
                .post(CREATE_ENDPOINT);

        courierMethods.printResponse(response, gson);
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
        System.out.println("Курьер не создан: не заполнены обязательные поля");
    }
}