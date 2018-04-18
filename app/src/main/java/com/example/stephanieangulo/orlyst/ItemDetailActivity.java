package com.example.stephanieangulo.orlyst;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ItemDetailActivity extends AppCompatActivity {
    TextView itemTitle;
    TextView itemDescription;
    TextView sellerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_item);
        String title = this.getIntent().getExtras().getString("itemTitle");
        String description = this.getIntent().getExtras().getString("itemDescription");
        String seller = this.getIntent().getExtras().getString("sellerName");

        itemTitle = findViewById(R.id.detail_item_title);
        itemDescription = findViewById(R.id.item_description_tv);
        sellerName = findViewById(R.id.seller_name_tv);

        itemTitle.setText(title);
        itemDescription.setText(description);
        sellerName.setText("by " + seller);
        sellerName.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

    }
}
