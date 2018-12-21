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
@Sql({"classpath:/clear.sql", "classpath:/clearorders.sql", "classpath:/clearbaskets.sql", "classpath:/clearusers.sql"})
public class YellowCodeBasketSameProductsTest {

    @Autowired
    private BasketController basketController;

    @Autowired
    private OrdersController ordersController;

    @Autowired
    private ProductController productController;

    @Autowired
    private UserController userController;

    @Before
    public void addProductsAndUser() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("testadmin", "admin", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        productController.createProduct(new Product(1, "Az aliceblue 50 árnyalata", "aliceblue",
                "E. L. Doe", 9999, ProductStatusType.ACTIVE, new Category(1, "Egyéb", 1L)));
        productController.createProduct(new Product(2, "Legendás programozók és megfigyelésük",
                "legendas", "J. K. Doe", 3999, ProductStatusType.ACTIVE, new Category(1, "Egyéb", 1L)));

        userController.createUser(new User(1, "user1", "Test One", "Elsőjelszó1", UserRole.ROLE_USER));

        SecurityContextHolder.getContext().setAuthentication(a);
    }

    @WithMockUser(username = "user1", roles = "USER")
    @Test
    public void tesAddSameProductsToBasket() {
        basketController.addToBasket(2, 5L);
        basketController.addToBasket(2, 2L);
        basketController.addToBasket(2, 9L);

        List<BasketProduct> myBasket = basketController.listProducts();
        assertEquals(Long.valueOf(16), myBasket.get(0).getQuantity());
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void testDeleteSameProductsFromBasket() {
        basketController.addToBasket(2, 1L);
        basketController.addToBasket(2, 1L);
        basketController.addToBasket(2, 1L);

        List<BasketProduct> myBasket = basketController.listProducts();
        assertEquals(Long.valueOf(3), myBasket.get(0).getQuantity());

        basketController.deleteSingleProduct(2);
        List<BasketProduct> myBasketAfterDelete = basketController.listProducts();
        assertEquals(0, myBasketAfterDelete.size());
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void testOrderSameProductsFromBasket() {
        basketController.addToBasket(2, 1L);
        basketController.addToBasket(2, 1L);
        basketController.addToBasket(2, 1L);

        ordersController.createOrderAndOrderItems("valami");

        List<OrderItem> orderItemList = ordersController.listOrderItems(1);

        assertEquals(3, orderItemList.get(0).getQuantity());
        assertEquals(3999, orderItemList.get(0).getProductPrice());
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void testincreaseBasketQuantityByOne() {
        basketController.addToBasket(2, 1L);
        basketController.increaseBasketQuantityByOne(2L, 1L);

        List<BasketProduct> myBasket = basketController.listProducts();
        assertEquals(Long.valueOf(2), myBasket.get(0).getQuantity());
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void testdecreaseBasketQuantityByOne() {
        basketController.addToBasket(2, 1L);
        basketController.addToBasket(2, 1L);
        basketController.addToBasket(2, 1L);

        basketController.decreaseBasketQuantityByOne(2L, 1L);

        List<BasketProduct> myBasket = basketController.listProducts();
        assertEquals(Long.valueOf(2), myBasket.get(0).getQuantity());
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void testSetBasketQuantity() {
        basketController.addToBasket(2, 1L);
        basketController.setBasketQuantity(2L, 1L, 5L);
        List<BasketProduct> myBasket = basketController.listProducts();

        assertEquals(myBasket.size(), 1);
        assertEquals(myBasket.get(0).getId(), 2L);
        assertEquals(myBasket.get(0).getName(), "Legendás programozók és megfigyelésük");
        assertEquals(myBasket.get(0).getCurrentPrice(), 3999);
        assertEquals(myBasket.get(0).getQuantity(), Long.valueOf(5));

    }

}