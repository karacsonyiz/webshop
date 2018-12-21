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
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql("classpath:/clearForFeedback.sql")
public class YellowCodeEditFeedbackTest {

    @Autowired
    private UserController userController;

    @Autowired
    private ProductController productController;

    @Autowired
    private FeedbackController feedbackController;

    @Autowired
    private FeedbackDao feedbackDao;

    @Autowired
    private OrdersController ordersController;

    @Autowired
    private BasketController basketController;

    @Before
    public void init(){
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("testadmin", "admin", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        productController.createProduct(new Product(1, "Az aliceblue 50 árnyalata", "aliceblue", "E. L. Doe", 9999, ProductStatusType.ACTIVE, new Category(1, "Egyéb", 1L)));
        productController.createProduct(new Product(2, "Legendás programozók és megfigyelésük", "legendas", "J. K. Doe", 3999, ProductStatusType.ACTIVE, new Category(1, "Egyéb", 1L)));
        productController.createProduct(new Product(3, "Az 50 első Trainer osztály", "osztaly", "Jack Doe", 5999, ProductStatusType.ACTIVE, new Category(1, "Egyéb", 1L)));
        userController.createUser(new User(1, "feedbackUser", "Feedback User", "Feedback1", UserRole.ROLE_USER ));

        SecurityContextHolder.getContext().setAuthentication(a);

        basketController.addToBasket(1, 1L);
        basketController.addToBasket(2, 1L);
        basketController.addToBasket(3, 1L);
        ordersController.createOrderAndOrderItems("szállítási cím");

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("testadmin", "admin", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        ordersController.modifyActiveStatusToDelivered(1);

        SecurityContextHolder.getContext().setAuthentication(a);
    }

    @Test
    @WithMockUser(username = "feedbackUser", roles = "USER")
    public void editFeedback() {
        User user = userController.getUser(SecurityContextHolder.getContext().getAuthentication());

        Feedback testFeedback = new Feedback(4, "Király", LocalDateTime.now(), user);
        feedbackController.createFeedback(testFeedback, 1);

        List<Feedback> feedbackList = feedbackDao.findFeedBacksByProductId(1);
        assertEquals(1, feedbackList.size());
        assertEquals(4, feedbackList.get(0).getRatingScore());
        assertEquals("Király", feedbackList.get(0).getRatingText());

        Response response = feedbackController.modifyFeedbackByUser(
                new Feedback(5, "Naggyon király", LocalDateTime.now(), user), 1);

        assertTrue(response.isValidRequest());
        assertEquals("Értékelés módosítva.", response.getMessage());

        feedbackList = feedbackDao.findFeedBacksByProductId(1);
        assertEquals(1, feedbackList.size());
        assertEquals(5, feedbackList.get(0).getRatingScore());
        assertEquals("Naggyon király", feedbackList.get(0).getRatingText());
    }

    @Test
    @WithMockUser(username = "feedbackUser", roles = "USER")
    public void deleteFeedback() {
        User user = userController.getUser(SecurityContextHolder.getContext().getAuthentication());

        Feedback testFeedback1 = new Feedback(5, "Király", LocalDateTime.now(), user);
        feedbackController.createFeedback(testFeedback1, 2);
        Feedback testFeedback2 = new Feedback(4, "Naggyon király", LocalDateTime.now(), user);
        Response response1 = feedbackController.createFeedback(testFeedback2, 1);
        assertTrue(response1.isValidRequest());
        assertEquals("Értékelés hozzáadva.", response1.getMessage());

        List<Feedback> feedbackList1 = feedbackDao.findFeedBacksByProductId(1);
        assertEquals(1, feedbackList1.size());
        assertEquals(4, feedbackList1.get(0).getRatingScore());
        assertEquals("Naggyon király", feedbackList1.get(0).getRatingText());

        Response response2 = feedbackController.deleteFeedbackByUser(1);
        assertTrue(response2.isValidRequest());
        assertEquals("Értékelés törölve.", response2.getMessage());

        feedbackList1 = feedbackDao.findFeedBacksByProductId(1);
        assertEquals(0, feedbackList1.size());

        List<Feedback> feedbackList2 = feedbackDao.findFeedBacksByProductId(2);
        assertEquals(1, feedbackList2.size());
    }
}
