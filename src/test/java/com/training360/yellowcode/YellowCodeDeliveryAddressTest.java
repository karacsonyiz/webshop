package com.training360.yellowcode;

import com.training360.yellowcode.dbTables.*;
import com.training360.yellowcode.userinterface.BasketController;
import com.training360.yellowcode.userinterface.OrdersController;
import com.training360.yellowcode.userinterface.ProductController;
import com.training360.yellowcode.userinterface.UserController;
import org.junit.Before;
import org.junit.Test;
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

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql("classpath:/clearForDeliveryTest.sql")
public class YellowCodeDeliveryAddressTest {

    @Autowired
    private OrdersController ordersController;

    @Autowired
    private ProductController productController;

    @Autowired
    private UserController userController;

    @Autowired
    private BasketController basketController;

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
        userController.createUser(new User(1, "user1", "Test One", "Elsőjelszó2", UserRole.ROLE_USER));

        SecurityContextHolder.getContext().setAuthentication(a);
    }

    @Test
    @WithMockUser(username = "admin1", roles = "ADMIN")
    public void testSingleDeliveryAddress() {
        basketController.addToBasket(1,3L);
        ordersController.createOrderAndOrderItems("Liget utca 5.");

        List<Orders> orders = ordersController.listOrders();

        assertEquals(orders.size(), 1);
        assertEquals("Liget utca 5.", orders.get(0).getDeliveryAddress());
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void testMultipleDeliveryAddresses() {
        basketController.addToBasket(1,3L);
        ordersController.createOrderAndOrderItems("Liget utca 9.");

        basketController.addToBasket(3,1L);
        ordersController.createOrderAndOrderItems("Kossuth utca 1.");

        List<String> deliveryAddresses = ordersController.listDeliveryAddressesOfUser();

        assertEquals(deliveryAddresses.size(), 2);
        assertEquals("Kossuth utca 1.", deliveryAddresses.get(1));
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void testSameDeliveryAddresses() {
        basketController.addToBasket(1,3L);
        ordersController.createOrderAndOrderItems("Liget utca 9.");

        basketController.addToBasket(3,1L);
        ordersController.createOrderAndOrderItems("Liget utca 9.");

        List<String> deliveryAddresses = ordersController.listDeliveryAddressesOfUser();

        assertEquals(deliveryAddresses.size(), 1);
        assertEquals("Liget utca 9.", deliveryAddresses.get(0));
    }
}

