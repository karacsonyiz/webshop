package com.training360.yellowcode.userinterface;

import com.training360.yellowcode.businesslogic.FeedbackService;
import com.training360.yellowcode.businesslogic.Response;
import com.training360.yellowcode.businesslogic.UserService;
import com.training360.yellowcode.dbTables.Feedback;
import com.training360.yellowcode.dbTables.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class FeedbackController {

    private FeedbackService feedbackService;
    private UserService userService;

    public FeedbackController(FeedbackService feedbackService, UserService userService) {
        this.feedbackService = feedbackService;
        this.userService = userService;
    }

    @RequestMapping(value = "/api/products/{productId}/feedback", method = RequestMethod.POST)
    public Response createFeedback(@RequestBody Feedback feedback, @PathVariable long productId) {
        User user = getAuthenticatedUser();
        if (user != null) {
            return feedbackService.createFeedback(feedback, productId, user);
        } else {
            return new Response(false, "Értékelés írásához kérjük, jelentkezz be!");
        }
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {     //nincs bejelentkezve
            return null;
        }
        User user = userService.findUserByUserName(authentication.getName()).get();
        return user;
    }

    @RequestMapping(value = "/api/products/{productId}/feedback", method = RequestMethod.DELETE)
    public Response deleteFeedbackByUser(@PathVariable long productId) {
        User user = getAuthenticatedUser();
        if (user != null) {
            return feedbackService.deleteFeedbackByUser(productId, user);
        } else {
            return new Response(false, "Értékelés törléséhez kérjük, jelentkezz be!");
        }
    }

    @RequestMapping(value = "/api/products/{productId}/edit-feedback", method = RequestMethod.POST)
    public Response modifyFeedbackByUser(@RequestBody Feedback feedback, @PathVariable long productId) {
        User user = getAuthenticatedUser();
        if (user != null) {
            return feedbackService.modifyFeedbackByUser(feedback, productId, user);
        } else {
            return new Response(false, "Értékelés módosításához kérjük, jelentkezz be!");
        }
    }
}
