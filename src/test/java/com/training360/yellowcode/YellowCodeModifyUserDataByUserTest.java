package com.training360.yellowcode;

import com.training360.yellowcode.businesslogic.UserService;
import com.training360.yellowcode.database.UserDao;
import com.training360.yellowcode.dbTables.User;
import com.training360.yellowcode.dbTables.UserRole;
import com.training360.yellowcode.userinterface.UserController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql(scripts = "classpath:/clearusers.sql")
public class YellowCodeModifyUserDataByUserTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @Before
    public void init() {
        userController.createUser(new User(1, "testuser", "Test One", "Elsőjelszó1", UserRole.ROLE_USER));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testUpdateUserByUserEveryData() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        User actualUser = userController.getUser(a);
        userService.updateUser(actualUser.getId(),"modifiedLoginName","changedPassword22");

        User modifiedUser = userDao.listUsers().get(0);
        assertEquals(modifiedUser.getId(), 1);
        assertEquals(modifiedUser.getFullName(), "modifiedLoginName");
    }
}
