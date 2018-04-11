package com.example.stephanieangulo.orlyst;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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

public class PostItemActivity extends AppCompatActivity {
    private static final String TAG = "PostItemActivity";

    private DatabaseReference mUserSellingItemReference;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private Context mContext;
    private Button postBtn;
    private Button backBtn;
    private ImageView itemImage;
    private EditText titleText;
    private EditText descriptionText;

    private boolean isTitleFilled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item);
        mContext = this;
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserSellingItemReference = FirebaseDatabase.getInstance().getReference()
                .child("selling-items").child(mUser.getUid());

        postBtn = findViewById(R.id.post_btn);
        backBtn = findViewById(R.id.back_btn);
        itemImage = findViewById(R.id.post_item_image);
        titleText = findViewById(R.id.post_item_title);
        descriptionText = findViewById(R.id.post_item_description);

        updateButtonStatus(false);
        addTextListeners();
        setThumbnail();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TakePhotoActivity.class);
                startActivity(intent);
            }
        });

    }
    protected void onPost(View view) {
        if(postBtn.isEnabled()) {
            System.out.println("Posting image woo");
            String title = titleText.getText().toString();
            String description = descriptionText.getText().toString();
            addItemToDatabase(title, description);
        }
    }
    private byte[] getImage() {
        if(getIntent().hasExtra("bytes")) {
            return this.getIntent().getByteArrayExtra("bytes");
        }
        return null;
    }
    private void setThumbnail() {
        ItemImage image = new ItemImage(getImage());
        itemImage.setImageBitmap(image.decodeToBitmap());

    }
    private void addItemToDatabase(String title, String description) {
        DatabaseReference specificSellingItemRef = mUserSellingItemReference.push();
        String key = specificSellingItemRef.getKey();
        Item item = new Item(title, description, mUser.getDisplayName(), mUser.getEmail(), key, getImage());
        Map<String, Object> itemValues = item.toMap();
        // TODO: figure out how to add bytes[] to firebase (https://firebase.google.com/docs/storage/android/upload-files)

        specificSellingItemRef.updateChildren(itemValues).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "Success!");
                        Toast.makeText(mContext, "Success!!! Check database for changes;)",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "FAIL");
                Toast.makeText(mContext, "fail :/ check logcat for error",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e(TAG, "Complete");
            }
        });
    }
    private void addTextListeners() {
        titleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isTitleFilled = false;
                if(s.length() != 0) {
                    isTitleFilled = true;
                }
                updateButtonStatus(isTitleFilled);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isTitleFilled = false;
                if(s.length() != 0) {
                    isTitleFilled = true;
                }
                updateButtonStatus(isTitleFilled);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void updateButtonStatus(boolean filled) {
        if(filled) {
            postBtn.setClickable(true);
            postBtn.setEnabled(true);
            postBtn.setAlpha(1f);
        } else {
            postBtn.setClickable(false);
            postBtn.setEnabled(false);
            postBtn.setAlpha(.2f);
        }
    }
}
