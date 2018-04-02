package com.example.stephanieangulo.orlyst;

import java.util.ArrayList;
import java.util.List;

public class SellingItem {
    private String itemName, description, user;

    public SellingItem() {
    }

    public SellingItem(String name, String description, String user) {
        this.itemName = name;
        this.description = description;
        this.user = user;
    }
    public String getItemName() {
        return itemName;
    }

    public void setName(String newName) {
        itemName = newName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String newDescription) {
        description = newDescription;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String newUser) {
        user = newUser;
    }
    public List<SellingItem> getTempData() {
        List<SellingItem> items = new ArrayList<>();

        SellingItem item = new SellingItem("Books",
                "Random text random text wooo Random text random text wooo Random text random text wooo", "Steph");
        SellingItem item1 = new SellingItem("Pencils",
                "More random text more random text more random text ", "Emily");
        SellingItem item2 = new SellingItem("Water bottle",
                "Steph doesn't deserve a new water bottle " +
                        "Steph doesn't deserve a new water bottle Steph " +
                        "doesn't deserve a new water bottle", "Vanessa");
        SellingItem item3 = new SellingItem("Pens",
                "Lalalalaalala lalalalalala lalalalalalal", "Nicole");
        SellingItem item4 = new SellingItem("iPhone adapter",
                "Techy tech tech Techy tech tech Techy tech tech", "Mario");
        SellingItem item5 = new SellingItem("Guitar",
                "I buy really expensive guitars, " +
                        "now I'm selling it cos I realized how expensive they are", "Luis");
        SellingItem item6 = new SellingItem("More textbooks",
                "Take my discrete textbook pls Random text random text wooo " +
                        "Random text random text wooo Random text random text wooo", "Steph");
        SellingItem item7 = new SellingItem("Caligraphy pens",
                "I got a caligraphy set for my birthday and i didn't like it " +
                        "More random text more random text more random text ", "Emily");
        SellingItem item8 = new SellingItem("Hydroflask",
                "Steph still doesn't deserve a new water bottle " +
                        "Steph still doesn't deserve a new water bottle Steph " +
                        "still doesn't deserve a new water bottle", "Vanessa");
        SellingItem item9 = new SellingItem("Red pens",
                "Let me edit your essay at the writing center", "Nicole");
        SellingItem item10 = new SellingItem("Circa survive vinyl",
                "I bought two lol", "Mario");
        SellingItem item11 = new SellingItem("Amp",
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
}
