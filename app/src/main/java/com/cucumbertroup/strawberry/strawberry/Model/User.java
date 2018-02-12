package com.cucumbertroup.strawberry.strawberry.Model;

/**
 * Created by Max on 28.11.2017.
 */

public class User {
    private String userName;
    private String email;

    public User() {
    }

    public User(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}