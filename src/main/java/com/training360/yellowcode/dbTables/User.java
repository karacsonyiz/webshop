package com.training360.yellowcode.dbTables;

public class User {

    private long id;
    private String loginName;
    private String fullName;
    private String password;
    private UserRole role = UserRole.ROLE_USER;

    public User(long id, String loginName, String fullName, String password, UserRole role) {
        this.id = id;
        this.loginName = loginName;
        this.fullName = fullName;
        this.password = password;
        this.role = role;
    }

    public User(long id, String loginName) {
        this.id = id;
        this.loginName = loginName;
    }

    public User(String loginName, String role) {
        this.loginName = loginName;
        this.role = UserRole.valueOf(role);
    }

    public User() {
    }

    public long getId() {
        return id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role.name();
    }

    public void setRole(String role) {
        this.role = UserRole.valueOf(role);
    }
}
