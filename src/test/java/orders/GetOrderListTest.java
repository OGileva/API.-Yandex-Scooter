package orders;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

@Epic("Яндекс Самокат")
@Feature("Тестирование API получения списка заказов")
@DisplayName("Получение списка заказов")
public class GetOrderListTest{

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Можно получить список всех заказов - возвращается в теле ответа")
    public void getAllOrders() {
        Response response = OrderApi.getAllOrders();
        OrderApi.checkResponseBody(response);
        response.then().assertThat().body("orders", hasSize(greaterThan(0))).and().statusCode(SC_OK);
    }
}