package com.example.stephanieangulo.orlyst;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Parcel
public class Item {
    private static final String TAG = "Item.java";
    private String itemName, description, seller, email, key, timestamp, sellerID, category, price;
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

    public List<Item> getTempData() {
        List<Item> items = new ArrayList<>();

        Item item = new Item("Books",
                "Random text random text wooo Random text random text wooo Random text random text wooo", "Steph");
        Item item1 = new Item("Pencils",
                "More random text more random text more random text ", "Emily");
        Item item2 = new Item("Water bottle",
                "Steph doesn't deserve a new water bottle " +
                        "Steph doesn't deserve a new water bottle Steph " +
                        "doesn't deserve a new water bottle", "Vanessa");
        Item item3 = new Item("Pens",
                "Lalalalaalala lalalalalala lalalalalalal", "Nicole");
        Item item4 = new Item("iPhone adapter",
                "Techy tech tech Techy tech tech Techy tech tech", "Mario");
        Item item5 = new Item("Guitar",
                "I buy really expensive guitars, " +
                        "now I'm selling it cos I realized how expensive they are", "Luis");
        Item item6 = new Item("More textbooks",
                "Take my discrete textbook pls Random text random text wooo " +
                        "Random text random text wooo Random text random text wooo", "Steph");
        Item item7 = new Item("Caligraphy pens",
                "I got a caligraphy set for my birthday and i didn't like it " +
                        "More random text more random text more random text ", "Emily");
        Item item8 = new Item("Hydroflask",
                "Steph still doesn't deserve a new water bottle " +
                        "Steph still doesn't deserve a new water bottle Steph " +
                        "still doesn't deserve a new water bottle", "Vanessa");
        Item item9 = new Item("Red pens",
                "Let me edit your essay at the writing center", "Nicole");
        Item item10 = new Item("Circa survive vinyl",
                "I bought two lol", "Mario");
        Item item11 = new Item("Amp",
                "I buy really expensive amps, " +
                        "now I'm selling it cos I realized how expensive they are", "Luis");

        items.add(item);
        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);
        items.add(item5);
        items.add(item6);
        items.add(item7);
        items.add(item8);
        items.add(item9);
        items.add(item10);
        items.add(item11);

        return items;
    }

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
        return result;
    }
}
