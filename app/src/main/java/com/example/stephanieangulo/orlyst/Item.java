package com.example.stephanieangulo.orlyst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Item {
    private String itemName, description, username, email, key;
    private byte[] bytes;

    public Item() {
    }
    public Item(String name, String description, String user) {
        this.itemName = name;
        this.description = description;
        this.username = user;
    }
    public Item(String name, String description, String username, String email, String key, byte[] bytes) {
        this.itemName = name;
        this.description = description;
        this.username = username;
        this.email = email;
        this.key = key;
        this.bytes = bytes;
    }
    public void setItemName(String newName) {
        itemName = newName;
    }
    public void setDescription(String newDescription) {
        description = newDescription;
    }
    public String getItemName() {
        return itemName;
    }
    public String getDescription() {
        return description;
    }
    public String getUser() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public String getKey() {
        return key;
    }
    public byte[] getBytes() {
        return bytes;
    }
//    public byte[] convertListToArray(List<Byte> list) {
//        Byte[] byteObjects = new Byte[list.size()];
//        byte[] converted = new byte[list.size()];
//
//        int i=0;
//        for(Byte b: byteObjects)
//            converted[i++] = b.byteValue();
//
//        return converted;
//    }
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
    public List<Item> getItemsFromDataBase() {
        // TODO: pull from firebase HERE.
        return null;
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        //Byte[] byteObjectArray = ArrayUtils.toObject(bytes);
        //List<Byte> list =  Arrays.asList(byteObjectArray);

        result.put("title", itemName);
        result.put("description", description);
        result.put("author", username);
        result.put("email", email);
        result.put("key", key);
        //result.put("bytes", list);
        return result;
    }
}
