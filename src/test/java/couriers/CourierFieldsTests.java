package couriers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static couriers.CourierConstants.*;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Epic("Яндекс.Самокат")
@Feature("Тестирование API создания курьера")
@DisplayName("Проверка обязательности заполнения полей")
public class CourierFieldsTests {

    private Gson gson;
    private int courierID = -1;
    private CourierApi courierApi = new CourierApi();

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
            courierApi.deleteCourier(courierID);
        }
    }

    @Test
    @DisplayName("Создание курьера с заполнение обязательных полей - логин и пароль")
    @Description("Успешное создание учетной записи. Код и статус ответа 201 Created")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateCourierWithAllRequiredFields() {
        // Создаем объект курьера
        Courier courier = new Courier("Monet", "1234", "Tejada");

        // Преобразуем объект курьера в JSON
        String body = gson.toJson(courier);

        // Отправляем POST запрос
        Response response = courierApi.createCourier(body);

        // Проверяем статус код и ответ
        assertThat(response.getStatusCode(), is(SC_CREATED)); // Проверка, что статус 201 Created
        assertThat(response.jsonPath().get("ok"), is(true));


        //Получаем ID курьера
        courierID = courierApi.getCourierId(courier.getLogin(), courier.getPassword());
        System.out.println("Успешное создание учетной записи с полями login, password, first name");
    }

    @Test
    @DisplayName("Создание курьера без поля login")
    @Description("Невозможно создать курьера без поля login. Код и статус ответа 400 Bad Request")
    @Severity(SeverityLevel.CRITICAL)
    public void courierCreationWithoutLoginTest() {
        //Создаем тело запроса без логина
        String bodyWithoutLogin = "{ \"password\": \"1234\", \"firstName\": \"Tejada\" }";
        //Задаем ожидаемое сообщение об ошибке
        String expectedMessage = "Недостаточно данных для создания учетной записи";

        //Отправляем POST запрос на создание курьера
        Response response = courierApi.createCourier(bodyWithoutLogin);

        courierApi.printResponse(response, gson);
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
        System.out.println("Курьер не создан: не заполнено поле login");
    }

    @Test
    @DisplayName("Создание курьера без поля password")
    @Description("Невозможно создать курьера без поля password. Код и статус ответа 400 Bad Request")
    @Severity(SeverityLevel.CRITICAL)
    public void courierCreationWithoutPasswordTest() {
        //Создаем тело запроса без пароля
        String bodyWithoutPassword = "{ \"login\": \"Diana\", \"firstName\": \"Tejada\" }";
        //Задаем ожидаемое сообщение об ошибке
        String expectedMessage = "Недостаточно данных для создания учетной записи";

        //Отправляем POST запрос на создание курьера
        Response response = courierApi.createCourier(bodyWithoutPassword);

        courierApi.printResponse(response, gson);
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
        System.out.println("Курьер не создан: не заполнено поле password");
    }

    @Test
    @DisplayName("Создание курьера без обязательных полей")
    @Description("Невозможно создать курьера без обязательных полей. Код и статус ответа 400 Bad Request")
    @Severity(SeverityLevel.CRITICAL)
    public void courierCreationWithoutRequiredFieldsTest() {
        //Создаем тело запроса без логина и пароля
        String bodyWithoutRequiredFields = "{\"firstName\": \"Tejada\" }";
        //Задаем ожидаемое сообщение об ошибке
        String expectedMessage = "Недостаточно данных для создания учетной записи";

        //Отправляем POST запрос на создание курьера
        Response response = courierApi.createCourier(bodyWithoutRequiredFields);

        courierApi.printResponse(response, gson);
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
        System.out.println("Курьер не создан: не заполнены обязательные поля");
    }
}