package com.training360.yellowcode;

import com.training360.yellowcode.businesslogic.UserService;
import com.training360.yellowcode.dbTables.User;
import com.training360.yellowcode.dbTables.UserRole;
import com.training360.yellowcode.userinterface.UserController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql(scripts = "classpath:/clearusers.sql")
@WithMockUser(username = "testadmin", roles = "ADMIN")
public class YellowcodeApplicationUserTests {

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    @Before
    public void init() {
        userController.createUser(new User(1, "testadmin", "Test Admin", "Testadmin1",UserRole.ROLE_ADMIN));
        userController.createUser(new User(2, "login1", "Test One", "Elsőjelszó1", UserRole.ROLE_USER));
        userController.createUser(new User(3, "login2", "Test Two", "Másodikjelszó2", UserRole.ROLE_USER));
        userController.createUser(new User(4, "login3", "Test Three", "harmadikJelszó3",UserRole.ROLE_USER));
    }

    @Test
    public void testListUsers() {
        List<User> users1 = userController.listUsers();

        assertEquals(users1.size(), 4);
    }

    @Test
    public void testCreateUsers() {
        List<User> users1 = userController.listUsers();

        userController.createUser(new User(5, "login4", "Test Four", "4nEgyEdikjelszó!", UserRole.ROLE_USER));

        List<User> users2 = userController.listUsers();

        assertEquals(users1.size(), 4);
        assertEquals(users2.size(), 5);
    }

    @Test
    public void testCreateUserWithExistingUserName() {
        List<User> users1 = userController.listUsers();
        userController.createUser(new User(5, "login3", "Test Four", "4nEgyEdikjelszó!", UserRole.ROLE_USER));

        List<User> users2 = userController.listUsers();

        assertEquals(users1.size(), 4);
        assertEquals(users2.size(), 4);
    }

    @Test
    public void testDeleteUser() {
        userController.createUser(new User(5, "login4", "Test Four", "4nEgyEdikjelszó!", UserRole.ROLE_USER));
        List<User> users1 = userController.listUsers();

        assertEquals(users1.size(), 5);

        userController.deleteUser(5);
        List<User> users2 = userController.listUsers();

        assertEquals(users2.size(), 4);
    }

    @Test
    public void testUpdateUser() {
        userController.createUser(new User(5, "login4", "Test Four", "4nEgyEdikjelszó!", UserRole.ROLE_USER));

        userController.updateUser(new User(5, "login4", "changed", "Új&Jelszó4", UserRole.ROLE_USER));
        List<User> users1 = userController.listUsers();

        assertEquals(users1.size(), 5);

        User login4User = userService.findUserByUserName("login4").get();
        assertEquals(login4User.getId(), 5);
        assertEquals(login4User.getFullName(), "changed");
    }



}
