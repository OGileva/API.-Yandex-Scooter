package orders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.Step;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import io.qameta.allure.internal.shadowed.jackson.databind.SerializationFeature;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static orders.OrderConstants.*;


public class OrderApi {

    //Создание заказа
    @Step("Создание заказа")
    public static Response createOrder(Order order) {
        Response response = given()
                .spec(Specifications.requestSpec())
                .header("Content-type", "application/json")
                .body(order)
                .post(CREATE_ORDER_ENDPOINT);
        return response;
    }

    // Новый метод для проверки поля 'track'
    private static Integer validateTrackNumber(Response response) {
        JsonPath jsonPath = new JsonPath(response.asString());
        Integer trackNumber = jsonPath.get("track");
        if (trackNumber == null) {
            throw new RuntimeException("Ошибка при создании заказа. Поле 'track' отсутствует в ответе. Тело ответа: " + response.asString());
        }
        return trackNumber;
    }

    // Новый метод для проверки кода ответа
    private static void checkResponseStatus(Response response, int expectedStatusCode) {
        if (response.getStatusCode() != expectedStatusCode) {
            throw new RuntimeException("Ошибка при создании заказа. Код ответа: " + response.getStatusCode() + ", Тело ответа: " + response.asString());
        }
    }

    //Проверка трек номера
    @Step("Сравнение ожидаемого кода ответа с фактическим")
    public static void checkTrackingNumber(Response response, int responseCode) {
        response.then().assertThat().body("track", not(0)).and().statusCode(responseCode);
    }

    //Получение списка заказов
    @Step("Получение списка заказов")
    public static Response getAllOrders() {
        Response response = given()
                .spec(Specifications.requestSpec())
                .header("Content-type", "application/json")
                .get(CREATE_ORDER_ENDPOINT);
        return response;
    }

    @Step("Проверка списка заказов в теле ответа")
    public static void checkResponseBody(Response response) {
        JsonPath jsonPath = new JsonPath(response.asString());
        List<Map<String, Object>> orders = jsonPath.getList("orders");

        Gson gson = new GsonBuilder().setPrettyPrinting().create(); // Создание экземпляра Gson с форматированием
        String ordersJson = gson.toJson(orders); // Преобразование списка заказов в JSON

        System.out.println("Список заказов получен. Код ответа: " + response.getStatusCode() + ", Тело ответа: " + response.asString());
        System.out.println(ordersJson); // Вывод JSON списка заказов
    }

}
