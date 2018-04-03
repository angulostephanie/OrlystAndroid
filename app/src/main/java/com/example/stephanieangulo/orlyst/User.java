package com.example.stephanieangulo.orlyst;

public class User {
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
}
