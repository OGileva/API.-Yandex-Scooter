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
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Яндекс Самокат")
@Feature("Тестирование API удаления курьера")
@DisplayName("Проверка возможности удаления курьера")

public class DeleteCourierTest {
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
    @DisplayName("Удаление курьера")
    @Description("Успешное удаление курьера. Код ответа 200")
    @Severity(SeverityLevel.CRITICAL)
    public void courierDeleteTest() {
        //Создаем курьера
        Courier courier = new Courier("Tariq", "1234", "StPatric");

        //Преобразуем объект курьера в JSON
        String body = gson.toJson(courier);

        //Выполняем POST запрос на создание курьера
        Response response = courierApi.createCourier(body);

        //Получаем ID курьера
        courierID = courierApi.getCourierId(courier.getLogin(), courier.getPassword());

        //Удаляем курьера
       Response deleteResponse = courierApi.deleteCourier(courierID);

       //Проверяем код и статус ответа
        assertThat(deleteResponse.getStatusCode(), is(SC_OK)); // Проверка, что статус 201 Created
        assertThat(deleteResponse.jsonPath().get("ok"), is(true));

        System.out.println("Курьер успешно удален");
        System.out.println("Код ответа: " + deleteResponse.getStatusCode());
    }

    @Test
    @DisplayName("Сообщение об ошибке при удалении курьера с несуществующим ID")
    @Description("При удалении курьера с несуществующим ID появляется сообщение \"Курьера с таким id нет\". Код ответа 404")
    @Severity(SeverityLevel.NORMAL)
    public void errorMessageResponseTest() {

        //Задаем несуществующее ID курьера
        courierID = 1234568989;

        //Удаляем курьера
        Response deleteResponse = courierApi.deleteCourier(courierID);

        //Проверяем код и статус ответа
        assertThat(deleteResponse.getStatusCode(), is(SC_NOT_FOUND));
        assertThat(deleteResponse.jsonPath().get("message"), is("Курьера с таким id нет."));

        System.out.println("Код ответа: " + deleteResponse.getStatusCode());
        System.out.println("Сообщение: " + deleteResponse.jsonPath().get("message"));
    }
}

