package com.example.stephanieangulo.orlyst;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class AddItemInfoActivity extends AppCompatActivity {
    Context mContext;
    Button postBtn;
    Button backBtn;
    ImageView itemImage;
    EditText titleText;
    EditText descriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);
        mContext = this;
        postBtn = findViewById(R.id.post_btn);
        backBtn = findViewById(R.id.back_btn);
        itemImage = findViewById(R.id.post_item_image);
        titleText = findViewById(R.id.post_item_title);
        descriptionText = findViewById(R.id.post_item_description);

        setThumbnail();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TakePhotoActivity.class);
                startActivity(intent);
            }
        });
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Posting image woo");


            }
        });
    }

    public void setThumbnail() {
        if(getIntent().hasExtra("bytes")) {
            byte[] bytes = this.getIntent().getByteArrayExtra("bytes");
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            itemImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false));
        }
    }
}
