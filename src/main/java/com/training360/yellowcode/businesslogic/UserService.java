package com.training360.yellowcode.businesslogic;

import com.training360.yellowcode.database.DuplicateUserException;
import com.training360.yellowcode.database.UserDao;
import com.training360.yellowcode.dbTables.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    private UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void createUser(User user) {
        if (findUserByUserName(user.getLoginName()).isPresent()) {
            throw new DuplicateUserException("A user with this login-name already exists.");
        }
        if (!new PasswordValidator().passwordStrengthValidator(user.getPassword())) {
            throw new IllegalArgumentException("Password is not valid");
        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        long generatedId = userDao.createUser(user);
        LOGGER.info(MessageFormat.format(
                "User added(id: {0}, loginName: {1}, fullName: {2}, password: {3}, role: {4})",
                generatedId,
                user.getLoginName(), user.getFullName(), user.getPassword(), user.getRole()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<User> listUsers() {
        return sortUsersByName(userDao.listUsers());
    }

    public void updateUser(long id, String name, String password) {
        if ((name != null && name.trim().length() == 0) || (password != null && (password.trim().length() == 0
                || !new PasswordValidator().passwordStrengthValidator(password)))) {
            throw new IllegalArgumentException("Invalid name or password.");
        }
        if (password != null) {
            password = new BCryptPasswordEncoder().encode(password);
        }
        userDao.updateUser(id, name, password);
        LOGGER.info(MessageFormat.format("User modified to -> id: {0}, fullName: {1}, password: {2}",
                id, name, password));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(long id) {
        userDao.deleteUser(id);
        LOGGER.info(MessageFormat.format("User (userId:{0}) removed", id));
    }

    public Optional<User> findUserByUserName(String userName) {
        return userDao.findUserByUserName(userName);
    }

    private List<User> sortUsersByName(List<User> users) {
        return users.stream()
                .sorted(Comparator.comparing(User::getLoginName))
                .collect(Collectors.toList());
    }
}
