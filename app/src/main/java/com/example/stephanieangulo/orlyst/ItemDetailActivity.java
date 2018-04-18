package com.example.stephanieangulo.orlyst;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ItemDetailActivity extends AppCompatActivity {
    TextView itemTitle;
    TextView itemDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_item);
        String title = this.getIntent().getExtras().getString("itemTitle");
        String description = this.getIntent().getExtras().getString("itemDescription");

        itemTitle = findViewById(R.id.detail_item_title);
        itemDescription = findViewById(R.id.item_description_tv);

        itemTitle.setText(title);
        itemDescription.setText(description);

    }
}
