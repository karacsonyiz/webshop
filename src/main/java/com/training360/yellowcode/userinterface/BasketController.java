package com.training360.yellowcode.userinterface;

import com.training360.yellowcode.businesslogic.BasketsService;
import com.training360.yellowcode.businesslogic.Response;
import com.training360.yellowcode.businesslogic.UserService;
import com.training360.yellowcode.dbTables.Basket;
import com.training360.yellowcode.dbTables.BasketProduct;
import com.training360.yellowcode.dbTables.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class BasketController {

    private BasketsService basketsService;
    private UserService userService;

    public BasketController(BasketsService basketsService, UserService userService) {
        this.basketsService = basketsService;
        this.userService = userService;
    }

    @RequestMapping(value = "/api/basket", method = RequestMethod.GET)
    public List<BasketProduct> listProducts() {
        User user = getAuthenticatedUser();
        if (user != null) {
            return basketsService.listProducts(user.getId());
        } else {
            return new ArrayList<>();
        }
    }

    @RequestMapping(value = "/api/basket/{productId}/{quantity}", method = RequestMethod.POST)
    public Response addToBasket(@PathVariable long productId, @PathVariable Long quantity) {
        User user = getAuthenticatedUser();
        if (user != null) {
            return basketsService.addToBasket(new Basket(user.getId(), productId, quantity));
        } else {
            return new Response(false, "A felhasználó nem jogosult a kosár módosítására.");
        }
    }

    @RequestMapping(value = "/api/basket", method = RequestMethod.DELETE)
    public Response deleteWholeBasket() {
        User user = getAuthenticatedUser();
        if (user != null) {
            return basketsService.deleteFromBasketByUserId(user.getId());
        } else {
            return new Response(false, "A felhasználó nem jogosult a törlésre.");
        }
    }

    @RequestMapping(value = "/api/basket/{productId}", method = RequestMethod.DELETE)
    public Response deleteSingleProduct(@PathVariable long productId) {
        User user = getAuthenticatedUser();
        if (user != null) {
            return basketsService.deleteFromBasketByUserIdAndProductId(user.getId(), productId);
        } else {
            return new Response(false, "A felhasználó nem jogosult a törlésre.");
        }
    }

    @RequestMapping(value = "/api/basket/{prouductId}/{quantity}/increase", method = RequestMethod.POST)
    public Response increaseBasketQuantityByOne(@PathVariable Long prouductId, @PathVariable Long quantity) {
        User user = getAuthenticatedUser();
        if (user != null) {
            basketsService.increaseBasketQuantityByOne(new Basket(user.getId(), prouductId, quantity));
            return new Response(true, "Módosítva");
        } else {
            return new Response(false, "A felhasználó nem jogosult a kosár módosítására.");
        }
    }

    @RequestMapping(value = "/api/basket/{prouductId}/{quantity}/decrease", method = RequestMethod.POST)
    public Response decreaseBasketQuantityByOne(@PathVariable Long prouductId, @PathVariable Long quantity) {
        User user = getAuthenticatedUser();
        if (user != null) {
            basketsService.decreaseBasketQuantityByOne(new Basket(user.getId(), prouductId, quantity));
            return new Response(true, "Módosítva");
        } else {
            return new Response(false, "A felhasználó nem jogosult a kosár módosítására.");
        }
    }

    @RequestMapping(value = "/api/basket/{productId}/{oldQuantity}/{newQuantity}", method = RequestMethod.POST)
    public Response setBasketQuantity(@PathVariable Long productId, @PathVariable Long oldQuantity,
                                      @PathVariable Long newQuantity) {
        User user = getAuthenticatedUser();
        if (user != null) {
            basketsService.setBasketQuantity(new Basket(user.getId(), productId, oldQuantity), newQuantity);
            return new Response(true, "Módosítva");
        } else {
            return new Response(false, "A felhasználó nem jogosult a kosár módosítására.");
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
}
