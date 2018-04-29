package com.example.stephanieangulo.orlyst;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import org.parceler.Parcels;

public class ProfileActivity extends AppCompatActivity {
    private User mDisplayedSeller;
    private ImageView mProfileImage;
    private TextView mSellerName;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mProfileImage = findViewById(R.id.seller_profile_image);
        mSellerName = findViewById(R.id.seller_name);
        mRecyclerView = findViewById(R.id.profile_recycler_view);
        mDisplayedSeller = Parcels.unwrap(getIntent().getParcelableExtra("User"));
        setUpProfilePage();

    }

    private void setUpProfilePage() {
        mSellerName.setText(mDisplayedSeller.getFirst() + " " + mDisplayedSeller.getLast());
    }

}
