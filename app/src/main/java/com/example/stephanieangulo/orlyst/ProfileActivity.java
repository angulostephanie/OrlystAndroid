package com.example.stephanieangulo.orlyst;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private User mDisplayedSeller;
    private ImageView profileImage;
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

        profileImage = findViewById(R.id.seller_profile_image);
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


}
