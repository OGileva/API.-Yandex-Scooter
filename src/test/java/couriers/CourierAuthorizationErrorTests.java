package couriers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static couriers.CourierConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.apache.http.HttpStatus.*;

@Epic("Яндекс.Самокат")
@Feature("Тестирование API авторизации курьера")
@DisplayName("Проверка возможности авторизации курьера")
public class CourierAuthorizationErrorTests {

    private Gson gson;
    private int courierID = -1;
    private CourierApi courierApi = new CourierApi();

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
        courierApi.createCourier(body);

        // Задаем ожидаемое сообщение об ошибке
        String expectedErrorMessage = "Недостаточно данных для входа";

        // Создаем запрос для авторизации без поля password
        String bodyWithoutLogin = "{\"password\": \"1234\" }";

        // Получаем ID курьера
        courierID = courierApi.getCourierId(courier.getLogin(), courier.getPassword());

        try {
            // Отправляем POST запрос для авторизации
            Response responseWithoutLogin = courierApi.authorizeCourier(bodyWithoutLogin);

            // Проверяем код и статус ответа
            courierApi.checkStatusCode(responseWithoutLogin, 400);
            courierApi.printResponse(responseWithoutLogin, gson);
            assertThat(responseWithoutLogin.jsonPath().getString("message"), is(expectedErrorMessage));

        } finally {
            courierApi.deleteCourier(courierID);
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
        courierApi.createCourier(body);

        // Получаем ID курьера
        courierID = courierApi.getCourierId(courier.getLogin(), courier.getPassword());

        try {
            // Создаем запрос для авторизации без поля password
            String bodyWithoutPassword = "{\"login\": \"Davis\" }";

            // Отправляем POST запрос для авторизации
            Response responseWithoutPassword = courierApi.authorizeCourier(bodyWithoutPassword);

            // Задаем ожидаемое сообщение об ошибке
            String expectedErrorMessage = "Недостаточно данных для входа";

            // Проверяем код и статус ответа
            courierApi.checkStatusCode(responseWithoutPassword, SC_BAD_REQUEST);
            assertThat(responseWithoutPassword.jsonPath().getString("message"), is(expectedErrorMessage));
        } finally {
            courierApi.deleteCourier(courierID);
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
        courierApi.createCourier(body);

        // Получаем ID курьера
        courierID = courierApi.getCourierId(courier.getLogin(), courier.getPassword());

        try {
            // Создаем запрос для авторизации без логина и пароля
            String bodyWithoutFields = "{}";
            // Отправляем POST запрос для авторизации
            Response responseWithoutFields = courierApi.authorizeCourier(bodyWithoutFields);
            // Задаем ожидаемое сообщение об ошибке
            String expectedErrorMessage = "Недостаточно данных для входа";

            // Проверяем код и статус ответа
            courierApi.checkStatusCode(responseWithoutFields, SC_BAD_REQUEST);
            courierApi.printResponse(responseWithoutFields, gson);
            assertThat(responseWithoutFields.jsonPath().getString("message"), is(expectedErrorMessage));

        } finally {
            courierApi.deleteCourier(courierID);
        }
    }

    @Test
    @DisplayName("Авторизация с некорректными данными")
    @Description("Появление сообщения об ошибке, если указаны несуществующие логин и пароль. Код и статус ответа 404 Not Found.")
    @Severity(SeverityLevel.NORMAL)
    public void incorrectCourierAuthorizationTest() {
        String incorrectBody = "{ \"login\": \"piuhguhrbihfl\", \"password\": \"12347\" }";

        Response response = courierApi.authorizeCourier(incorrectBody);

        assertThat(response.getStatusCode(), is(SC_NOT_FOUND));
        assertThat(response.jsonPath().getString("message"), is("Учетная запись не найдена"));

        System.out.println("Not existent user login:");
        System.out.println("Response Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.asString());
    }
}
