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
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql("classpath:/clearForRecommendationTest.sql")
public class YellowCodeRecommendationTest {

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

        userController.createUser(new User(1, "user1", "Test One", "Elsőjelszó1", UserRole.ROLE_USER));

        SecurityContextHolder.getContext().setAuthentication(a);
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void testLastThreeProductsWithSingleOrder() {
        basketController.addToBasket(1,3L);
        basketController.addToBasket(3,2L);
        basketController.addToBasket(5,1L);
        basketController.addToBasket(2,3L);

        ordersController.createOrderAndOrderItems("Fő utca 4.");

        List<Product> lastThreeProducts = productController.showLastThreeSoldProducts();

        assertEquals(lastThreeProducts.size(), 3);
        assertEquals("Az aliceblue 50 árnyalata", lastThreeProducts.get(0).getName());
        assertEquals("Az 50 első Trainer osztály", lastThreeProducts.get(1).getName());
        assertEquals("Junior most és mindörökké", lastThreeProducts.get(2).getName());
    }
    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void testLastThreeProductsWithMultipleOrders() {
        basketController.addToBasket(1,3L);
        basketController.addToBasket(3,2L);
        basketController.addToBasket(5,1L);
        basketController.addToBasket(2,3L);

        ordersController.createOrderAndOrderItems("Liget utca 5.");

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ie){
            throw new RuntimeException("Sleep method is interrupted!",ie);
        }

        basketController.addToBasket(4,1L);
        basketController.addToBasket(3,3L);

        ordersController.createOrderAndOrderItems("Liget utca 5.");

        List<Product> lastThreeProducts = productController.showLastThreeSoldProducts();
        assertEquals(lastThreeProducts.size(), 3);
        assertEquals("Hogyan neveld a junior fejlesztődet", lastThreeProducts.get(0).getName());
        assertEquals("Az aliceblue 50 árnyalata", lastThreeProducts.get(1).getName());
        assertEquals("Az 50 első Trainer osztály", lastThreeProducts.get(2).getName());

    }
}
