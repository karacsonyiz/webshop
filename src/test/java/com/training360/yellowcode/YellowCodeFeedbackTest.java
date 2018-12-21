package com.training360.yellowcode;

import com.training360.yellowcode.businesslogic.Response;
import com.training360.yellowcode.database.FeedbackDao;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql("classpath:/clearForFeedback.sql")
public class YellowCodeFeedbackTest {

    @Autowired
    private UserController userController;

    @Autowired
    private ProductController productController;

    @Autowired
    private BasketController basketController;

    @Autowired
    private OrdersController ordersController;

    @Autowired
    private FeedbackController feedbackController;

    @Before
    public void init(){
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("testadmin", "admin", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        productController.createProduct(new Product(1, "Az aliceblue 50 árnyalata", "aliceblue", "E. L. Doe", 9999, ProductStatusType.ACTIVE, new Category(1, "Egyéb", 1L)));
        productController.createProduct(new Product(2, "Legendás programozók és megfigyelésük", "legendas", "J. K. Doe", 3999, ProductStatusType.ACTIVE, new Category(1, "Egyéb", 1L)));
        productController.createProduct(new Product(3, "Az 50 első Trainer osztály", "osztaly", "Jack Doe", 5999, ProductStatusType.ACTIVE, new Category(1, "Egyéb", 1L)));
        productController.createProduct(new Product(4, "Hogyan neveld a junior fejlesztődet", "junior", "Jane Doe", 6499, ProductStatusType.ACTIVE, new Category(1, "Egyéb", 1L)));
        productController.createProduct(new Product(5, "Junior most és mindörökké", "mindorokke", "James Doe", 2999, ProductStatusType.ACTIVE, new Category(1, "Egyéb", 1L)));

        userController.createUser(new User(1, "feedbackUser", "Feedback User", "Feedback1", UserRole.ROLE_USER ));
        userController.createUser(new User(2, "user1", "Test Two", "Másodikjelszó2", UserRole.ROLE_USER));
        userController.createUser(new User(3, "user2", "Test Three", "harmadikJelszó3", UserRole.ROLE_USER));

        SecurityContextHolder.getContext().setAuthentication(a);
    }


    @Test
    public void testCreateFeedbackAsUnregistered() {
        Feedback testFeedback = new Feedback(4, "Naggyon király", LocalDateTime.now(), null);
        Response response = feedbackController.createFeedback(testFeedback, 1);

        assertEquals("Értékelés írásához kérjük, jelentkezz be!", response.getMessage());
        assertFalse(response.isValidRequest());
    }


    @Test
    @WithMockUser(username = "feedbackUser", roles = "USER")
    public void testCreateFeedbackWithoutDeliveredProduct() {
        User user = userController.getUser(SecurityContextHolder.getContext().getAuthentication());
        Feedback testFeedback = new Feedback(4, "Naggyon király", LocalDateTime.now(), user);
        Response response = feedbackController.createFeedback(testFeedback, 1);

        assertEquals("Kizárólag olyan termékeket tud értékelni, amelyeket már kiszállítottunk Önnek.", response.getMessage());
        assertFalse(response.isValidRequest());

        List<Feedback> feedbacks = productController.findProductByAddress("aliceblue").get().getFeedbacks();
        assertEquals(0, feedbacks.size());
    }


    @Test
    @WithMockUser(username = "feedbackUser", roles = "USER")
    public void testFeedbackByBuyer() {
        User user = userController.getUser(SecurityContextHolder.getContext().getAuthentication());
        basketController.addToBasket(1, 1L);
        ordersController.createOrderAndOrderItems("szállítási cím");

        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("testadmin", "admin", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
        ordersController.modifyActiveStatusToDelivered(1);
        SecurityContextHolder.getContext().setAuthentication(a);

        Response response = feedbackController.createFeedback(new Feedback(4, "Naggyon király", LocalDateTime.now(), user), 1 );

        assertEquals("Értékelés hozzáadva.", response.getMessage());
        assertTrue(response.isValidRequest());

        List<Feedback> feedbacks = productController.findProductByAddress("aliceblue").get().getFeedbacks();

        assertEquals(1, feedbacks.size());
        assertEquals("Naggyon király", feedbacks.get(0).getRatingText());
        assertEquals(4, feedbacks.get(0).getRatingScore());
    }


    @Test
    @WithMockUser(username = "feedbackUser", roles = "USER")
    public void testCreateSecondFeedbackSameUser() {
        User user = userController.getUser(SecurityContextHolder.getContext().getAuthentication());

        basketController.addToBasket(1, 1L);
        ordersController.createOrderAndOrderItems("szállítási cím");

        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("testadmin", "admin", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
        ordersController.modifyActiveStatusToDelivered(1);
        SecurityContextHolder.getContext().setAuthentication(a);

        Feedback testFeedback = new Feedback(4, "Naggyon király", LocalDateTime.now(), user);
        Response response1 = feedbackController.createFeedback(testFeedback, 1);
        assertEquals("Értékelés hozzáadva.", response1.getMessage());
        assertTrue(response1.isValidRequest());

        Response response2 = feedbackController.createFeedback(testFeedback, 1);
        assertEquals("A megadott terméket már értékelte, amennyiben módosítani szeretné értékelését, a szerkesztés gombra kattintva megteheti.",
                response2.getMessage());
        assertFalse(response2.isValidRequest());
    }


    @Test
    @WithMockUser(username = "feedbackUser", roles = "USER")
    public void testListFeedback() {
        User user = userController.getUser(SecurityContextHolder.getContext().getAuthentication());

        basketController.addToBasket(1, 1L);
        ordersController.createOrderAndOrderItems("szállítási cím");

        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("testadmin", "admin", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
        ordersController.modifyActiveStatusToDelivered(1);
        SecurityContextHolder.getContext().setAuthentication(a);

        Feedback testFeedback = new Feedback(5, "Még nagyobb király", LocalDateTime.now(), user);
        feedbackController.createFeedback(testFeedback, 1);

        List<Feedback> allFeedbacks = productController.findProductByAddress("aliceblue").get().getFeedbacks();

        assertEquals(1, allFeedbacks.size());
        assertEquals(5, allFeedbacks.get(0).getRatingScore());
        assertEquals("Még nagyobb király", allFeedbacks.get(0).getRatingText());
        assertEquals(user.getLoginName(), allFeedbacks.get(0).getUser().getLoginName());
    }



}
