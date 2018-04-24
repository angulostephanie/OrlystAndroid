package com.example.stephanieangulo.orlyst;

import java.util.ArrayList;
import java.util.List;

public class User {
    private List<String> allUsers = new ArrayList<>();
    public String first;
    public String last;
    public String email;
    public String userID;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String first, String last, String email, String userID) {
        this.first = first;
        this.last = last;
        this.email = email;
        this.userID = userID;
    }

    protected void addUsers(String id) {
        allUsers.add(id);
    }
    protected List<String> getAllUsers() {
        return allUsers;
    }
}
