package com.example.stephanieangulo.orlyst;

import org.parceler.Parcel;

import java.util.HashMap;
import java.util.Map;

@Parcel
public class User {
    private Map<String, Item> items = new HashMap<>();
    private Map<String, Item> watchlist = new HashMap<>();
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
    public void setItems(Map<String, Item> items) {
        this.items = items;
    }
    public void setWatchlist(Map<String, Item> watchlist) {
        this.watchlist = watchlist;
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
    public  Map<String, Item> getItems() { return items; }
    public  Map<String, Item> getWatchlist() { return watchlist; }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("first", first);
        result.put("last", last);
        result.put("email", email);
        result.put("userID", userID);
        result.put("items", items);
        result.put("watchlist", watchlist);
        return result;
    }
}
