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
import static couriers.CourierConstants.COURIER_CREATE_ENDPOINT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Яндекс.Самокат")
@Feature("Тестирование API создания курьера")
@DisplayName("Проверка возможности создания курьера")
public class CourierCreationTests {

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
    @DisplayName("Создание курьера")
    @Description("Успешное создание учетной записи. Код и статус ответа 201 Created")
    @Severity(SeverityLevel.CRITICAL)
    public void creationCourierTest() {
        // Создаем объект курьера
        Courier courier = new Courier("Tariq", "1234", "StPatric");

        // Преобразуем объект курьера в JSON
        String body = gson.toJson(courier);

        // Выполняем POST запрос для создания курьера
        Response response = courierApi.createCourier(body);

        // Проверяем статус код и ответ
        courierApi.checkStatusCode(response, 201);
        assertThat(response.jsonPath().get("ok"), is(true));

        // Получаем ID курьера
        courierID = courierApi.getCourierId(courier.getLogin(), courier.getPassword());
        if(courierID != -1) {
            System.out.println("Учетная запись курьера успешно создана в системе");
        }
    }

    @Test
    @DisplayName("Успешный запрос возвращает ok: true")
    @Severity(SeverityLevel.NORMAL)
    public void successfulCreationResponseBodyTest() {
        // Создаем объект курьера
        Courier courier = new Courier("James", "1234", "StPatric");

        // Преобразуем объект курьера в JSON
        String body = gson.toJson(courier);

        // Выполняем POST запрос для создания курьера
        Response response = courierApi.createCourier(body);

        //Форматируем тело ответа
        String formattedResponseBody = courierApi.formatResponseBody(response.getBody().asString());

        // Проверяем, что ответ содержит "ok: true"
        assertThat(formattedResponseBody, containsString("\"ok\": true"));
        System.out.println("Возвращается тело ответа ok:true");

        // Получаем ID курьера
        courierID = courierApi.getCourierId(courier.getLogin(), courier.getPassword());
    }

    @Test
    @DisplayName("Успешный запрос возвращает код ответа 201 Created")
    @Severity(SeverityLevel.NORMAL)
    public void successfulCreationStatusCode201Test() {
        // Создаем объект курьера
        Courier courier = new Courier("Tasha", "1234", "StPatric");

        // Преобразуем объект курьера в JSON
        String body = gson.toJson(courier);

        // Выполняем POST запрос для создания курьера
        Response response = courierApi.createCourier(body);

        // Проверяем статус код и ответ
        courierApi.checkStatusCode(response, 201);
        System.out.println("Код ответа 201 Created");

        // Получаем ID курьера
        courierID = courierApi.getCourierId(courier.getLogin(), courier.getPassword());
    }
}