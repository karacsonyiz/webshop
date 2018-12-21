package com.training360.yellowcode.userinterface;

import com.training360.yellowcode.businesslogic.Response;
import com.training360.yellowcode.businesslogic.UserService;
import com.training360.yellowcode.database.DuplicateUserException;
import com.training360.yellowcode.dbTables.User;
import com.training360.yellowcode.dbTables.UserRole;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/api/user")
    public User getUser(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String name = userDetails.getUsername();
            String role = new ArrayList<GrantedAuthority>(userDetails.getAuthorities()).get(0).getAuthority();
            User foundUser = userService.findUserByUserName(name).get();
            return new User(foundUser.getId(), name, foundUser.getFullName(), foundUser.getPassword(), UserRole.valueOf(role));
        }
    }

    @RequestMapping(value = "/api/users", method = RequestMethod.GET)
    public List<User> listUsers() {
        return userService.listUsers();
    }

    @RequestMapping(value = "/api/users", method = RequestMethod.POST)
    public Response createUser(@RequestBody User user) {
        try {
            userService.createUser(user);
            return new Response(true, "Sikeres regisztráció.");
        } catch (DuplicateUserException dpe) {
            return new Response(false, "A megadott felhasználónév már foglalt.");
        } catch (IllegalArgumentException iae) {
            return new Response(false, "A megadott jelszó nem felel meg a feltételeknek.");
        }
    }

    @RequestMapping(value = "/api/users/update", method = RequestMethod.POST)
    public Response updateUser(@RequestBody User user) {
        User authenticatedUser = getAuthenticatedUser();
        if (authenticatedUser != null && (authenticatedUser.getRole().equals("ROLE_ADMIN") || authenticatedUser.getId() == user.getId())) {
            try {
                userService.updateUser(user.getId(), user.getFullName(), user.getPassword());
                return new Response(true, "Sikeresen frissítve.");
            } catch (IllegalArgumentException iae) {
                return new Response(false, "Érvénytelen név vagy jelszó.");
            }
        } else {
            return new Response(false, "Nem jogosult az adott felhasználó adatainak módosítására.");
        }
    }

    @RequestMapping(value = "/api/users/{id}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
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
