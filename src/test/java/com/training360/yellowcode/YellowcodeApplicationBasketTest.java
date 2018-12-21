package com.training360.yellowcode;

import com.training360.yellowcode.dbTables.*;
import com.training360.yellowcode.userinterface.BasketController;
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
@Sql(scripts = "classpath:/clearbaskets.sql")
public class YellowcodeApplicationBasketTest {

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
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void testBasketDatas() {
        Basket basket = new Basket(1L, 2, 5, 1L);
        assertEquals(basket.getId(), Long.valueOf(1));
        assertEquals(basket.getUserId(), 2);
        assertEquals(basket.getProductId(), 5);
        assertEquals(1L, basket.getQuantity().longValue());
    }

    @Test
    @WithMockUser(username = "admin1", roles = "ADMIN")
    public void testListBasketProductsWithEmptyBasket() {
        List<BasketProduct> basketItems = basketController.listProducts();
        assertEquals(basketItems.size(), 0);
    }

    @WithMockUser(username = "user1", roles = "USER")
    @Test
    public void testAddToBasketThenListBasketProducts() {
        basketController.addToBasket(4, 1L);

        List<BasketProduct> basketItems = basketController.listProducts();
        assertEquals(basketItems.size(), 1);
        assertEquals(basketItems.get(0).getName(), "Hogyan neveld a junior fejlesztődet");
    }

    @WithMockUser(username = "user1", roles = "USER")
    @Test
    public void testAddProductsMultipleTimesToBasket() {
        basketController.addToBasket(1, 1L);
        basketController.addToBasket(1, 1L);
        basketController.addToBasket(1, 1L);

        List<BasketProduct> basketItems = basketController.listProducts();
        assertEquals(basketItems.size(), 1);
    }

    @Test
    @WithMockUser(username = "admin1", roles = "ADMIN")
    public void testDeleteFromBasketByUserId() {
        basketController.addToBasket(1, 1L);
        basketController.addToBasket(2, 1L);


        List<BasketProduct> basketItems1 = basketController.listProducts();
        assertEquals(basketItems1.size(), 2);

        basketController.deleteWholeBasket();
        List<BasketProduct> basketItems2 = basketController.listProducts();
        assertEquals(basketItems2.size(), 0);

    }

    @Test
    @WithMockUser(username = "user2", roles = "USER")
    public void testDeleteFromBasketByProductIdAndUserId() {
        basketController.addToBasket(1, 1L);
        basketController.addToBasket(4, 1L);
        basketController.addToBasket(5, 1L);

        List<BasketProduct> basketItems1 = basketController.listProducts();
        assertEquals(basketItems1.size(), 3);

        basketController.deleteSingleProduct(4);
        List<BasketProduct> basketItems2 = basketController.listProducts();
        assertEquals(basketItems2.size(), 2);

    }

}