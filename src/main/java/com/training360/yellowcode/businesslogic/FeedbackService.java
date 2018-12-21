package com.training360.yellowcode.businesslogic;

import com.training360.yellowcode.database.FeedbackDao;
import com.training360.yellowcode.database.ProductDao;
import com.training360.yellowcode.dbTables.Feedback;
import com.training360.yellowcode.dbTables.User;
import org.springframework.stereotype.Service;


@Service
public class FeedbackService {

    private FeedbackDao feedbackDao;
    private ProductDao productDao;

    public FeedbackService(FeedbackDao feedbackDao, ProductDao productDao) {
        this.feedbackDao = feedbackDao;
        this.productDao = productDao;
    }

    public Response createFeedback(Feedback feedback, long productId, User user) {
        if(feedbackDao.didUserReviewProduct(productId, user.getId())) {
            return new Response(false, "A megadott terméket már értékelte, amennyiben módosítani szeretné értékelését, a szerkesztés gombra kattintva megteheti.");
        }
        if (!feedbackDao.hasUserReceivedProduct(productId, user.getId())) {
            return new Response(false, "Kizárólag olyan termékeket tud értékelni, amelyeket már kiszállítottunk Önnek.");
        }
        feedback.setUser(user);
        feedbackDao.createFeedback(feedback, productId);
        return new Response(true, "Értékelés hozzáadva.");
    }

    public Response deleteFeedbackByUser(long productId, User user) {
        if (!feedbackDao.didUserReviewProduct(productId, user.getId())) {
            return new Response(false, "Csak a saját értékelését törölheti!");
        }
        feedbackDao.deleteFeedbackByUser(productId, user.getId());
        return new Response(true, "Értékelés törölve.");
    }

    public Response modifyFeedbackByUser(Feedback feedback, long productId, User user) {
        if (!feedbackDao.didUserReviewProduct(productId, user.getId())) {
            return new Response(false, "Csak a saját értékelését módosíthatja!");
        }
        feedback.setUser(user);
        feedbackDao.modifyFeedbackByUser(feedback, productId);
        return new Response(true, "Értékelés módosítva.");
    }
}
