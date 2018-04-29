package com.example.stephanieangulo.orlyst;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ItemDetailActivity extends AppCompatActivity {
    private Context mContext;
    private TextView itemTitle;
    private TextView itemDescription;
    private TextView itemSeller;
    private ImageView itemImage;
    private Button watchlistBtn;
    private Button contactBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_item);
        mContext = this;

        itemTitle = findViewById(R.id.detail_item_title);
        itemDescription = findViewById(R.id.item_description_tv);
        itemSeller = findViewById(R.id.seller_name_tv);
        itemImage = findViewById(R.id.detail_item_image);
        watchlistBtn = findViewById(R.id.watchlist_btn);
        contactBtn = findViewById(R.id.contact_btn);

        String title = this.getIntent().getExtras().getString("itemTitle");
        String description = this.getIntent().getExtras().getString("itemDescription");
        String seller = this.getIntent().getExtras().getString("sellerName");
        byte[] jpeg = this.getIntent().getExtras().getByteArray("bytes");

        itemTitle.setText(title);
        itemDescription.setText(description);
        if(jpeg != null)
            setItemImage(jpeg);
        itemSeller.setText("by " + seller);
        itemSeller.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

    }

    private void setItemImage(byte[] jpeg) {
        Glide.with(mContext)
                .load(jpeg)
                .asBitmap()
                .into(itemImage);

    }

    private User getUser(String sellerID) {

        return null;
    }
}
