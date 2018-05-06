package com.example.stephanieangulo.orlyst;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
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
    private Button sellerItemsBtn;
    private Button sellerWatchlistBtn;
    private Context mContext;
    private ActionBar toolbar;
    private boolean onSellerItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = findViewById(R.id.seller_profile_image);
        sellerName = findViewById(R.id.seller_name);
        recyclerView = findViewById(R.id.profile_recycler_view);
        backBtn = findViewById(R.id.profile_back_btn);
        sellerItemsBtn = findViewById(R.id.sellers_items);
        sellerWatchlistBtn = findViewById(R.id.sellers_watchlist);

        toolbar = getSupportActionBar();
        toolbar.setTitle("");
        
        onSellerItems = true;
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
        updateRecyclerView();

        backBtn.setOnClickListener(v -> {
            Intent backIntent = new Intent();
            setResult(RESULT_OK, backIntent);
            finish();
        });
        sellerItemsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSellerItems = true;
                updateRecyclerView();
            }
        });
        sellerWatchlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSellerItems = true;
                updateRecyclerView();
            }
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
    private void updateRecyclerView() {
        if(onSellerItems) {
            mItems = new ArrayList<>(mDisplayedSeller.getItems().values());
            mItems.sort(Comparator.comparing(Item::getTimestamp));
            Collections.reverse(mItems);
            mAdapter = new UserListAdapter(mContext, mItems);
        } else {
            mItems = new ArrayList<>(mDisplayedSeller.getWatchlist().values());
            mItems.sort(Comparator.comparing(Item::getTimestamp));
            Collections.reverse(mItems);
            mAdapter = new UserListAdapter(mContext, mItems);
        }

        setRecyclerView();
        mAdapter.notifyDataSetChanged();
    }

}
