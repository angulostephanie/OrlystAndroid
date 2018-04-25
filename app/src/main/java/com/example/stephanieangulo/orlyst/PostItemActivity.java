package com.example.stephanieangulo.orlyst;

import android.app.ProgressDialog;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Map;

public class PostItemActivity extends AppCompatActivity {
    private static final String TAG = "PostItemActivity";

    private DatabaseReference mItemReference;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private Context mContext;
    private Button postBtn;
    private Button backBtn;
    private ImageView itemImage;
    private EditText titleText;
    private EditText descriptionText;

    private boolean isTitleFilled;
    private boolean photoUploaded = false;
    private boolean infoUploaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item);
        mContext = this;
        mAuth = AppData.firebaseAuth;
        mUser = mAuth.getCurrentUser();
        //mItemReference = AppData.getItemReference(mUser.getUid());
        mItemReference = AppData.itemRootReference;
        mStorage = AppData.firebaseStorage;
        storageReference = mStorage.getReference();

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
    public void onPost(View view) {
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
        Glide.with(mContext)
                .load(getImage())
                .asBitmap()
                .placeholder(R.drawable.spinner)
                .fitCenter()
                .into(itemImage);
    }
    private void addItemToDatabase(String title, String description) {
        DatabaseReference postItemRef = mItemReference.push();
        String key = postItemRef.getKey();
        Item item = new Item(title, description, mUser.getDisplayName(),
                mUser.getEmail(), key);

        Map<String, Object> itemValues = item.toMap();

        uploadImage(getImage(), key);

        postItemRef.updateChildren(itemValues).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "Successfully uploaded item info!");
                        infoUploaded = true;
                        returnToNewsFeed(photoUploaded);
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
    private void uploadImage(byte[] image, String key) {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference ref = storageReference.child("images/"+ key);
        ref.putBytes(image)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        photoUploaded = true;
                        returnToNewsFeed(infoUploaded);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(PostItemActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    }
                });
    }
    private void returnToNewsFeed(boolean success) {
        if(success) {
            Toast.makeText(PostItemActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
        }
    }
    private void addTextListeners() {
        titleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isTitleFilled = s.length() != 0;

                updateButtonStatus(isTitleFilled);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isTitleFilled = s.length() != 0;

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
