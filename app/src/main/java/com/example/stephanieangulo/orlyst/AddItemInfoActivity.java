package com.example.stephanieangulo.orlyst;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class AddItemInfoActivity extends AppCompatActivity {
    private static final String TAG = "AddItemInfoActivity";
    private DatabaseReference mUserSellingItemReference;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private Context mContext;
    private Button postBtn;
    private Button backBtn;
    private ImageView itemImage;
    private EditText titleText;
    private EditText descriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);
        mContext = this;
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserSellingItemReference = FirebaseDatabase.getInstance().getReference().child("selling-items").child(mUser.getUid());

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
                String title = titleText.getText().toString();
                String description = descriptionText.getText().toString();
                if(!isCompletelyFilled(title, description))
                    Toast.makeText(mContext, "PLEASE FILL OUT EVERYTHING", Toast.LENGTH_SHORT).show();
                else
                    addItemToDB(title, description);
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

    public void addItemToDB(final String title, final String description) {
        DatabaseReference specificSellingItemRef = mUserSellingItemReference.push();
        String key = specificSellingItemRef.getKey();
        SellingItem item = new SellingItem(title, description, mUser.getDisplayName(), mUser.getEmail(), key);
        Map<String, Object> itemValues = item.toMap();
        specificSellingItemRef.updateChildren(itemValues).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "Success !");
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "FAIL");
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e(TAG, "Complete");
            }
        });
    }
    private boolean isCompletelyFilled(String title, String description) {
        return !TextUtils.isEmpty(title) && !TextUtils.isEmpty(description);
    }
}
