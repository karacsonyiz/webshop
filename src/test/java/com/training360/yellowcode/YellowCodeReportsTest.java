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
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql("classpath:/clearForReportTest.sql")
@WithMockUser(username = "testadmin", roles = "ADMIN")
public class YellowCodeReportsTest {

    @Autowired
    private OrdersController ordersController;

    @Autowired
    private BasketController basketController;

    @Autowired
    private ProductController productController;

    @Autowired
    private UserController userController;

    @Autowired
    private ReportsController reportsController;

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

        ordersController.modifyActiveStatusToDelivered(3);
    }

    @Test
    public void testReportByDate() {
        List<Reports> allReports = reportsController.listReportsByDate();
        Reports report1 = allReports.get(0);
        Reports report2 = allReports.get(1);

        assertEquals(2, allReports.size());

        assertEquals(4, report1.getProductCount());
        assertEquals(23996, report1.getTotalPrice());
        assertEquals(OrderStatus.ACTIVE, report1.getStatus());

        assertEquals(3, report2.getProductCount());
        assertEquals(20497, report2.getTotalPrice());
        assertEquals(OrderStatus.DELIVERED, report2.getStatus());
    }

    @Test
    public void testReportByProduct() {
        List<Reports> allReports = reportsController.listReportsByProductAndDate();
        Reports report = allReports.get(0);

        assertEquals(3, allReports.size());
        assertEquals(1, report.getProductCount());
        assertEquals("Az aliceblue 50 árnyalata", report.getProductName());
        assertEquals(9999, report.getProductPrice());
    }



}
