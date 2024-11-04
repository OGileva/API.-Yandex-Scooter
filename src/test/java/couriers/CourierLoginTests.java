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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.apache.http.HttpStatus.*;

@Epic("Яндекс.Самокат")
@Feature("Тестирование API авторизации курьера")
@DisplayName("Проверка возможности авторизации курьера")
public class CourierLoginTests {

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
    @DisplayName("Курьер может авторизоваться")
    @Description("Курьер авторизован, если указаны верные данные. Код ответа 200 ОК.")
    @Severity(SeverityLevel.CRITICAL)
    public void courierAuthorizationTest() {

        //Создаем курьера
        Courier courier = new Courier("Drew", "1234");
        String body = gson.toJson(courier);
        courierApi.createCourier(body);

        //Создаем запрос для авторизации
        String loginBody = "{ \"login\": \"Drew\", \"password\": \"1234\" }";

        //Отправляем POST запрос для авторизации
        Response loginResponse = courierApi.authorizeCourier(loginBody);

        //Проверяем код ответа
        courierApi.checkStatusCode(loginResponse, SC_OK);
        System.out.println("Курьер успешно авторизован");

        courierID = courierApi.getCourierId(courier.getLogin(), courier.getPassword());
    }

    @Test
    @DisplayName("Получение ID курьера")
    @Description("При успешной авторизации в ответе возвращается ID курьера")
    @Severity(SeverityLevel.CRITICAL)
    public void courierAuthorizationGetIdTest() {

        //Создаем курьера
        Courier courier = new Courier("Kane", "1234");
        String body = gson.toJson(courier);
        courierApi.createCourier(body);

        //Создаем запрос для авторизации
        String loginBody = "{ \"login\": \"Kane\", \"password\": \"1234\" }";

        //Отправляем POST запрос для авторизации
        Response loginResponse = courierApi.authorizeCourier(loginBody);

        //Получаем ID курьера
        courierID = courierApi.getCourierId(courier.getLogin(), courier.getPassword());
        // Проверяем ID курьера получен корректно
        assertThat(courierID, is(not(-1)));
        System.out.println("Успешный запрос возвращает ID курьера " + loginResponse.asString());
    }
}
