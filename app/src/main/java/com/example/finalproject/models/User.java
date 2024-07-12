package com.example.finalproject.models;

public class User {
    private String Name, Password, UserType;

    public User() {}

    public User(String name, String password, String userType) {
        this.Name = name;
        this.Password = password;
        this.UserType = userType;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getUserType() {
        return UserType;
    }

    public void setUserType(String userType) {
        UserType = userType;
    }
}
