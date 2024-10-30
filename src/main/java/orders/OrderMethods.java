package orders;

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


public class OrderMethods {

    //Создание заказа
    @Step("Создание заказа")
    public static Response createOrder(Order order) {
        Response response = given()
                .spec(Specifications.requestSpec())
                .header("Content-type", "application/json")
                .body(order)
                .post(CREATE_ORDER_ENDPOINT);
        //Код ответа
        if(response.getStatusCode() != 201) {
            throw new RuntimeException("Ошибка при создании заказа. Код ответа: " + response.getStatusCode() + ", Тело ответа: " + response.asString());
        }
        //Тело ответа
        JsonPath jsonPath = new JsonPath(response.asString());
        Integer trackNumber = jsonPath.get("track");
        if (trackNumber == null) {
            throw new RuntimeException("Ошибка при создании заказа. Поле 'track' отсутствует в ответе. Тело ответа: " + response.asString());
        }
        System.out.println("Заказ создан. Код ответа: " + response.getStatusCode() + ", Тело ответа: " + response.asString() + ", номер заказа: " + trackNumber);
        return response;
    }

    //Проверка трек номера
    @Step("Сравнение ожидаемого кода ответа с фактическим")
    public static void checkTrackingNumber(Response response, int responseCode) {
        response.then().assertThat().body("track", not(0)).and().statusCode(responseCode);
    }

    //Получение ID заказа
    @Step("Получение ID заказа по трек-номеру")
    public static String getOrderID(Response response) {

        // Получение трек-номера заказа
        String trackNumber = response.then().extract().body().asString();
        JsonPath jsonPath = new JsonPath(trackNumber);

        // Запрос на получение ID заказа по трек-номеру
        Response trackResponse = given()
                .spec(Specifications.requestSpec())
                .header("Content-type", "application/json")
                .get(GET_ORDER_ENDPOINT + "?track=" + jsonPath.getString("track"));

        // Проверка успешного получения ID заказа
        if (trackResponse.getStatusCode() != 200) {
            throw new RuntimeException("Ошибка при получении ID заказа. Код ответа: " + trackResponse.getStatusCode() + ", Тело ответа: " + trackResponse.asString());
        }

        // Извлечение ID заказа из ответа
        JsonPath orderJson = new JsonPath(trackResponse.asString());
        String orderId = orderJson.getString("id");
        if (orderId == null) {
            throw new RuntimeException("Ошибка: ID заказа отсутствует в ответе. Тело ответа: " + trackResponse.asString());
        }
        return orderId;
    }

    //Завершение заказа
    @Step("Завершение заказа по ID")
    public static Response deleteOrder(String id) {
        Response response = given()
                .spec(Specifications.requestSpec())
                .header("Content-type", "application/json")
                .put(CANCEL_ORDER_ENDPOINT + "?id=" + id);

        // Проверка кода ответа
        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Ошибка при удалении заказа. Код ответа: " + response.getStatusCode() + ", Тело ответа: " + response.asString());
        }

        // Вывод информации об успешном удалении
        System.out.println("Заказ удалён. Код ответа: " + response.getStatusCode() + ", Тело ответа: " + response.asString());
        return response;
    }


    //Проверка удаления заказа
    @Step("Проверка удаления заказа")
    public static void comparingSuccessfulOrderCancel(Response response, int expectedResponseCode) {
        if (response.getStatusCode() != expectedResponseCode) {
            throw new AssertionError("Ожидаемый код ответа: " + expectedResponseCode + ", Фактический код ответа: " + response.getStatusCode());
        }
        response.then().assertThat().body("ok", equalTo(true)).and().statusCode(expectedResponseCode);
    }

    //Получение списка заказов
    @Step("Получение списка заказов")
    public static Response getAllOrders() {
        Response response = given()
                .spec(Specifications.requestSpec())
                .header("Content-type", "application/json")
                .get(CREATE_ORDER_ENDPOINT);
        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Ошибка при получении списка заказов. Код ответа: " + response.getStatusCode() + ", Тело ответа: " + response.asString());
        }
        JsonPath jsonPath = new JsonPath(response.asString());
        List<Map<String, Object>> orders = jsonPath.getList("orders");
        if (orders == null || orders.isEmpty()) {
            throw new RuntimeException("Ошибка: список заказов пуст.");
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            String ordersJson = objectMapper.writeValueAsString(orders);
            System.out.println("Список заказов получен. Код ответа: " + response.getStatusCode() + ", Тело ответа: " + response.asString());
            System.out.println(ordersJson);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при преобразовании списка заказов в JSON: " + e.getMessage());
        }
        return response;
    }
}
