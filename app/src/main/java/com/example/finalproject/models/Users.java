package com.example.finalproject.models;

public class Users{
    private String Id;
    private User User;

    public Users(String id, User user){
        Id = id;
        User = user;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public User getUser() {
        return User;
    }

    public void setUser(User user) {
        User = user;
    }
}
