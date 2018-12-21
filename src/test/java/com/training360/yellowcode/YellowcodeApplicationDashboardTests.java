package com.training360.yellowcode;

import com.training360.yellowcode.dbTables.*;
import com.training360.yellowcode.userinterface.*;
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
@Sql(scripts = "classpath:/clearForDashboardTest.sql")
public class YellowcodeApplicationDashboardTests {

    @Autowired
    private DashboardController dashboardController;

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

        productController.createProduct(new Product(1, "Az aliceblue 50 árnyalata", "aliceblue", "E. L. Doe", 9999, ProductStatusType.ACTIVE, new Category(1, "Egyéb", 1L)));
        productController.createProduct(new Product(2, "Legendás programozók és megfigyelésük", "legendas", "J. K. Doe", 3999, ProductStatusType.ACTIVE, new Category(1, "Egyéb", 1L)));
        productController.createProduct(new Product(3, "Az 50 első Trainer osztály", "osztaly", "Jack Doe", 5999, ProductStatusType.ACTIVE, new Category(1, "Egyéb", 1L)));
        productController.createProduct(new Product(4, "Hogyan neveld a junior fejlesztődet", "junior", "Jane Doe", 6499, ProductStatusType.ACTIVE, new Category(1, "Egyéb", 1L)));
        productController.createProduct(new Product(5, "Junior most és mindörökké", "mindorokke", "James Doe", 2999, ProductStatusType.ACTIVE, new Category(1, "Egyéb", 1L)));

        userController.createUser(new User(1, "admin1", "Test One", "Elsőjelszó1", UserRole.ROLE_ADMIN));
        userController.createUser(new User(2, "user1", "Test Two", "Másodikjelszó2", UserRole.ROLE_USER));
        userController.createUser(new User(3, "user2", "Test Three", "harmadikJelszó3", UserRole.ROLE_USER));

        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user1", "user", List.of(new SimpleGrantedAuthority("ROLE_USER"))));

        basketController.addToBasket(1, 1L);
        basketController.addToBasket(2, 1L);
        ordersController.createOrderAndOrderItems("valami");

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user2", "user", List.of(new SimpleGrantedAuthority("ROLE_USER"))));

        basketController.addToBasket(2, 1L);
        basketController.addToBasket(3, 1L);
        ordersController.createOrderAndOrderItems("valami");

        basketController.addToBasket(1, 1L);
        basketController.addToBasket(4, 1L);
        basketController.addToBasket(2, 1L);
        ordersController.createOrderAndOrderItems("valami");

        SecurityContextHolder.getContext().setAuthentication(a);
    }

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    public void testDashboardDatasFromInit() {
        Dashboard dashboard = dashboardController.createDashboard();
        assertEquals(dashboard.getProductCount(), 5);
        assertEquals(dashboard.getActiveProductCount(), 5);
        assertEquals(dashboard.getOrderCount(), 3);
        assertEquals(dashboard.getActiveOrderCount(), 3);
        assertEquals(dashboard.getUserCount(), 2);
    }

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    public void productCounter() {
        Dashboard dashboard = dashboardController.createDashboard();
        assertEquals(dashboard.getProductCount(), 5);
    }

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    public void testActiveProductCounter() {
        productController.deleteProduct(1);
        productController.deleteProduct(2);
        Dashboard dashboard = dashboardController.createDashboard();
        assertEquals(dashboard.getProductCount(), 5);
        assertEquals(dashboard.getActiveProductCount(), 3);
    }

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    public void testOrderCounter() {
        Dashboard dashboard = dashboardController.createDashboard();
        assertEquals(dashboard.getOrderCount(), 3);
    }

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    public void testActiveOrderCounter() {
        Dashboard dashboard1 = dashboardController.createDashboard();
        assertEquals(dashboard1.getOrderCount(), 3);

        ordersController.deleteOrder(1);
        ordersController.modifyActiveStatusToDelivered(2);

        Dashboard dashboard2 = dashboardController.createDashboard();
        assertEquals(dashboard2.getOrderCount(), 3);
        assertEquals(dashboard2.getActiveOrderCount(), 1);
    }

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    public void testUserCounter() {
        Dashboard dashboard1 = dashboardController.createDashboard();
        assertEquals(dashboard1.getUserCount(), 2);

        userController.createUser(new User(4, "user3", "Test Four", "negyediKJelszó4", UserRole.ROLE_USER));
        Dashboard dashboard2 = dashboardController.createDashboard();
        assertEquals(dashboard2.getUserCount(), 3);

        userController.deleteUser(4);
        Dashboard dashboard3 = dashboardController.createDashboard();
        assertEquals(dashboard3.getUserCount(), 2);
    }



}
