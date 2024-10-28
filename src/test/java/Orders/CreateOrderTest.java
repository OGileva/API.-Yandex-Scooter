package Orders;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.apache.http.HttpStatus.*;

@RunWith(Parameterized.class)
@Epic("Яндекс Самокат")
@Feature("Тестирование API создания заказа")
@DisplayName("Создание заказа")
public class CreateOrderTest {
    private final String firstName;
    private final String lastName;
    private final String address;
    private final String metroStation;
    private final String phone;
    private final String deliveryDate;
    private final String comment;
    private final String[] color;
    private final int rentTime;

    public CreateOrderTest(String firstName, String lastName, String address, String metroStation,
                          String phone, int rentTime, String deliveryDate, String comment, String[] color) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][] getOrderData() {
        return new Object[][] {
                {"Monet", "Tejada", "5th avenue 666", "1", "+7 999 888 77 66", 5, "01.11.2024", "Don`t call", new String[]{"BLACK"}},
                {"Kane", "Tejada", "5th avenue 666", "1", "+7 999 888 77 66", 5, "01.11.2024", "Don`t call", new String[]{"GRAY"}},
                {"Diana", "Tejada", "5th avenue 666", "1", "+7 999 888 77 66", 5, "01.11.2024", "Don`t call", new String[]{"BLACK", "GRAY"}},
                {"Drew", "Tejada", "5th avenue 666", "1", "+7 999 888 77 66", 5, "01.11.2024", "Don`t call", new String[]{}},
        };
    }

    @Test
    @DisplayName("Создание заказа с выбором цвета самоката")
    @Description("Заказ можно создать, в ответе возвращается track")
    public void createOrderWithScooterColorTest() {

        Order order = new Order(firstName, lastName, address,
                metroStation, phone, deliveryDate, comment, color, rentTime);

        Response response = OrderMethods.createOrder(order);
        OrderMethods.checkTrackingNumber(response, SC_CREATED);
    }
}
