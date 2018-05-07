package com.example.stephanieangulo.orlyst;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private User mDisplayedSeller;
    private ImageView profileImageView;
    private UserListAdapter mAdapter;
    private TextView sellerName;
    private RecyclerView recyclerView;
    private List<Item> mItems;
    private ImageButton backBtn;
    private Context mContext;
    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImageView= findViewById(R.id.seller_profile_image);
        sellerName = findViewById(R.id.seller_name);
        recyclerView = findViewById(R.id.profile_recycler_view);
        backBtn = findViewById(R.id.profile_back_btn);

        toolbar = getSupportActionBar();
        toolbar.setTitle("");

        mContext = this;
        mDisplayedSeller = Parcels.unwrap(getIntent().getParcelableExtra("User"));
        mItems = new ArrayList<>(mDisplayedSeller.getItems().values());
        mItems.sort(Comparator.comparing(Item::getTimestamp));
        Collections.reverse(mItems);

        mAdapter = new UserListAdapter(mContext,
                new ArrayList<>(mDisplayedSeller.getItems().values()));

        toolbar.setLogo(R.drawable.small_orlyst_logo);
        toolbar.setDisplayUseLogoEnabled(true);
        toolbar.setDisplayShowHomeEnabled(true);

        setUpProfilePage();
        setRecyclerView();
        if(mDisplayedSeller.getProfilePicture() != null)
            fetchProfilePhoto(mDisplayedSeller.getProfilePicture());

        backBtn.setOnClickListener(v -> {
            Intent backIntent = new Intent();
            setResult(RESULT_OK, backIntent);
            finish();
        });

    }

    private void setUpProfilePage() {
        sellerName.setText(mDisplayedSeller.getFirst() + " " + mDisplayedSeller.getLast());

    }
    private void setRecyclerView(){
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);
    }
    private void fetchProfilePhoto(String key) {
        StorageReference storageRef = AppData.firebaseStorage.getReference();
        StorageReference pathRef = storageRef.child("images/");
        StorageReference imageRef = pathRef.child(key);
        final long LIMIT = 512 * 512;
        imageRef.getBytes(LIMIT).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Log.d(TAG, "successfully got the image!");
                loadProfilePicture(bytes);
                mAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "No profile pic sady");
                //test.setImageFound(false);

            }
        }).addOnCompleteListener(new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {

            }
        });
    }
    private void loadProfilePicture(byte[] bytes) {
        Glide.with(mContext)
                .load(bytes)
                .asBitmap()
                .placeholder(R.drawable.loading_spinner)
                .into(profileImageView);
    }


}
