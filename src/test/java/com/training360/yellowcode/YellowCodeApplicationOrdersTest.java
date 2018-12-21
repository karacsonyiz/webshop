package com.training360.yellowcode;

import com.training360.yellowcode.businesslogic.Response;
import com.training360.yellowcode.dbTables.*;
import com.training360.yellowcode.userinterface.BasketController;
import com.training360.yellowcode.userinterface.OrdersController;
import com.training360.yellowcode.userinterface.ProductController;
import com.training360.yellowcode.userinterface.UserController;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql({"classpath:/clear.sql", "classpath:/clearorders.sql", "classpath:/clearbaskets.sql", "classpath:/clearusers.sql"})
public class YellowCodeApplicationOrdersTest {

    @Autowired
    private OrdersController ordersController;

    @Autowired
    private BasketController basketController;

    @Autowired
    private ProductController productController;

    @Autowired
    private UserController userController;


    @Before
    public void init() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("testadmin", "admin", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        productController.createProduct(new Product(1, "Az aliceblue 50 árnyalata", "aliceblue", "E. L. Doe", 9999, ProductStatusType.ACTIVE, new Category(1, "Egyéb", 1L)));
        productController.createProduct(new Product(2, "Legendás programozók és megfigyelésük", "legendas", "J. K. Doe", 3999, ProductStatusType.ACTIVE, new Category(1, "Egyéb", 1L)));
        productController.createProduct(new Product(3, "Az 50 első Trainer osztály", "osztaly", "Jack Doe", 5999, ProductStatusType.ACTIVE, new Category(1, "Egyéb", 1L)));
        productController.createProduct(new Product(4, "Hogyan neveld a junior fejlesztődet", "junior", "Jane Doe", 6499, ProductStatusType.ACTIVE, new Category(1, "Egyéb", 1L)));
        productController.createProduct(new Product(5, "Junior most és mindörökké", "mindorokke", "James Doe", 2999, ProductStatusType.ACTIVE, new Category(1, "Egyéb", 1L)));

        userController.createUser(new User(1, "admin1", "Test One", "Elsőjelszó1", UserRole.ROLE_ADMIN));
        userController.createUser(new User(2, "user1", "Test Two", "Másodikjelszó2", UserRole.ROLE_USER));
        userController.createUser(new User(3, "user2", "Test Three", "harmadikJelszó3", UserRole.ROLE_USER));

        SecurityContextHolder.getContext().setAuthentication(a);

        basketController.addToBasket(1, 1L);
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void testListOrdersByUserIdWithoutExistingOrder() {
        List<Orders> ordersList = ordersController.listOrdersByUserId();

        assertEquals(ordersList.size(), 0);
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void testListOrdersByUserId() {
        ordersController.createOrderAndOrderItems("valami");
        List<Orders> ordersList1 = ordersController.listOrdersByUserId();
        assertEquals(ordersList1.size(), 1);

        basketController.addToBasket(2, 1L);
        ordersController.createOrderAndOrderItems("valami");
        List<Orders> ordersList2 = ordersController.listOrdersByUserId();
        assertEquals(ordersList2.size(), 2);
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void testListOrderItems() {
        basketController.addToBasket(2, 1L);
        ordersController.createOrderAndOrderItems("valami");

        List<OrderItem> orderItemList = ordersController.listOrderItems(1);
        assertEquals(orderItemList.size(), 2);
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void testOrderItemDatas() {
        ordersController.createOrderAndOrderItems("valami");

        List<OrderItem> orderItemList = ordersController.listOrderItems(1);
        assertEquals(orderItemList.get(0).getProductName(), "Az aliceblue 50 árnyalata");
        assertEquals(orderItemList.get(0).getProductPrice(), 9999);
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void testEmptyBasketAfterOrder() {
        basketController.addToBasket(2, 1L);
        basketController.addToBasket(3, 1L);

        ordersController.createOrderAndOrderItems("valami");
        List<Orders> ordersList = ordersController.listOrdersByUserId();
        List<OrderItem> orderItemList = ordersController.listOrderItems(1);

        assertEquals(ordersList.size(), 1);
        assertEquals(orderItemList.size(), 3);

        List<BasketProduct> basketItems = basketController.listProducts();
        assertEquals(basketItems.size(), 0);
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void testOrderWithEmptyBasket() {
        basketController.deleteSingleProduct(1);
        assertEquals(0, basketController.listProducts().size());
        Response response = ordersController.createOrderAndOrderItems("valami");
        assertEquals(response.getMessage(), "A kosár üres");
        assertFalse(response.isValidRequest());
    }

    @Test
    @WithMockUser(username = "admin1", roles = "ADMIN")
    public void deleteOrder() {
        ordersController.createOrderAndOrderItems("valami");
        List<Orders> orders = ordersController.listOrders();
        assertEquals(1, orders.size());
        ordersController.deleteOrder(orders.get(0).getId());
        orders = ordersController.listActiveOrders();
        assertEquals(0, orders.size());
    }

    @Test
    @WithMockUser(username = "admin1", roles = "ADMIN")
    public void deleteOrderItem() {
        basketController.addToBasket(2, 1L);
        basketController.addToBasket(3, 1L);
        ordersController.createOrderAndOrderItems("valami");
        List<Orders> orders = ordersController.listOrders();
        assertEquals(1, orders.size());
        List<OrderItem> orderItems = ordersController.listOrderItems(orders.get(0).getId());
        assertEquals(3, orderItems.size());
        ordersController.deleteOrderItem(orders.get(0).getId(), orderItems.get(0).getProductAddress());
        orderItems = ordersController.listOrderItems(orders.get(0).getId());
        assertEquals(2, orderItems.size());
    }

    @Test
    @WithMockUser(username = "admin1", roles = "ADMIN")
    public void deliveredOrderTest() {
        basketController.addToBasket(2, 1L);
        basketController.addToBasket(3, 1L);
        ordersController.createOrderAndOrderItems("valami");
        List<Orders> orders = ordersController.listOrders();
        assertEquals(1, orders.size());
        List<OrderItem> orderItems = ordersController.listOrderItems(orders.get(0).getId());
        assertEquals(3, orderItems.size());
        ordersController.modifyActiveStatusToDelivered(orders.get(0).getId());
        orders = ordersController.listOrders();
        assertEquals(OrderStatus.DELIVERED, orders.get(0).getStatus());
    }

}
