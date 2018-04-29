package com.example.stephanieangulo.orlyst;

import java.util.ArrayList;
import java.util.List;

public class User {
    private List<String> allUsers = new ArrayList<>();
    private String first, last, email, userID;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String first, String last, String email, String userID) {
        this.first = first;
        this.last = last;
        this.email = email;
        this.userID = userID;
    }

    public void setFirst(String first) {
        this.first = first;
    }
    public void setLast(String last) {
        this.last = last;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFirst() {
        return first;
    }

    public String getLast() {
        return last;
    }

    public String getEmail() {
        return email;
    }

    public String getUserID() {
        return userID;
    }
}
