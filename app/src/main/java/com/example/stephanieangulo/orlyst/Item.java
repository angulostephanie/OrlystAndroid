package com.example.stephanieangulo.orlyst;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Item {
    private static final String TAG = "Item.java";
    private String itemName, description, author, email, key, timestamp;


    public Item() {
    }
    public Item(String name, String description, String author) {
        this.itemName = name;
        this.description = description;
        this.author = author;
    }
    public Item(String name, String description, String author, String email, String key) {
        this.itemName = name;
        this.description = description;
        this.author = author;
        this.email = email;
        this.key = key;
    }

    public void setItemName(String newName) {
        this.itemName = newName;
    }
    public void setDescription(String newDescription) {
        this.description = newDescription;
    }
    public void setAuthor(String newAuthor) {
        this.author = newAuthor;
    }
    public void setTimestamp(String newTimestamp) {
        this.timestamp = newTimestamp;
    }
    public void setEmail(String newEmail) {
        this.email = newEmail;
    }
    public void setKey(String newKey) {
        this.key = newKey;
    }

    public String getItemName() {
        return itemName;
    }
    public String getDescription() {
        return description;
    }
    public String getAuthor() {
        return author;
    }
    public String getEmail() {
        return email;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public String getKey() {
        return key;
    }

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
    public static List<Item> getItemsFromDataBase() {
        final List<Item> items = new ArrayList<>();
        final DatabaseReference itemsRef = AppData.firebaseDatabase.getReference("items");

        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for(DataSnapshot data: dataSnapshots) {
                    String key = data.getKey();
                    DataSnapshot a = dataSnapshot.child(key);
                    Item item = a.getValue(Item.class);
                    items.add(item);
                    Log.d(TAG, "hello " +item.itemName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getDetails());
            }
        });

       return items;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("itemName", itemName);
        result.put("description", description);
        result.put("author", author);
        result.put("email", email);
        result.put("key", key);
        result.put("timestamp", ServerValue.TIMESTAMP.get("timestamp"));
        return result;
    }
}
