package com.example.stephanieangulo.orlyst;

import org.parceler.Parcel;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Parcel
public class Item {
    private static final String TAG = "Item.java";
    private String itemName, description, seller, email, key, timestamp, sellerID, category, price;
    private Map<String, String> peopleWatching = new HashMap<>();
    private boolean onWatchlist;
    private boolean isImageFound = true;
    private byte[] bytes;

    public Item() {
    }
    public Item(String name, String description, String seller) {
        this.itemName = name;
        this.description = description;
        this.seller = seller;
    }
    public Item(String name, String description, String seller, String email, String key,
                String sellerID, String category, String price) {
        this.itemName = name;
        this.description = description;
        this.seller = seller;
        this.sellerID = sellerID;
        this.email = email;
        this.key = key;
        this.category = category;
        this.price = price;
    }
    public Item(Item item, boolean onWatchlist) {
        this.itemName = item.getItemName();
        this.description = item.getDescription();
        this.seller = item.getSeller();
        this.sellerID = item.getSellerID();
        this.email = item.getEmail();
        this.bytes = item.getBytes();
        this.key = item.getKey();
        this.category = item.getCategory();
        this.timestamp = item.getTimestamp();
        this.onWatchlist = onWatchlist;
    }

    public String createTimestamp() {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        String timestamp = now.toString();
        return timestamp;
    }

    public void setPeopleWatching(Map<String, String> ID) { this.peopleWatching = ID; }
    public void setItemName(String newName) {
        this.itemName = newName;
    }
    public void setDescription(String newDescription) {
        this.description = newDescription;
    }
    public void setSeller(String newSeller) {
        this.seller = newSeller;
    }
    public void setSellerID(String newSellerID) {
        this.sellerID = newSellerID;
    }
    public void setEmail(String newEmail) {
        this.email = newEmail;
    }
    public void setKey(String newKey) {
        this.key = newKey;
    }
    public void setCategory(String newCategory) {
        this.category = newCategory;
    }
    public void setOnWatchlist(boolean onWatchlist) {
        this.onWatchlist = onWatchlist;
    }
    public void setImageFound(boolean isImageFound) { this.isImageFound = isImageFound; }
    public void setPrice(String newPrice) { this.price = newPrice; }
    public void setBytes(byte[] newBytes) { this.bytes = newBytes; }
    public void setTimestamp(String newTimestamp) {
        this.timestamp = newTimestamp;
    }

    public Map<String, String> getPeopleWatching() { return peopleWatching; }
    public String getItemName() {
        return itemName;
    }
    public String getDescription() {
        return description;
    }
    public String getSeller() {
        return seller;
    }
    public String getSellerID() { return sellerID; }
    public String getEmail() {
        return email;
    }
    public String getKey() {
        return key;
    }
    public String getCategory() {
        return category;
    }
    public String getPrice() { return price; }
    public byte[] getBytes() { return bytes; }
    public String getTimestamp() {
        return timestamp;
    }
    public boolean isOnWatchlist() { return onWatchlist; }
    public boolean isImageFound() { return isImageFound; }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("itemName", itemName);
        result.put("description", description);
        result.put("seller", seller);
        result.put("sellerID", sellerID);
        result.put("email", email);
        result.put("key", key);
        result.put("category", category);
        result.put("price", price);
        result.put("timestamp", createTimestamp());
        result.put("peopleWatching", peopleWatching);
        return result;
    }
}
