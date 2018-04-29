package com.example.stephanieangulo.orlyst;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.parceler.Parcels;

public class ProfileActivity extends AppCompatActivity {
    private User mDisplayedSeller;
    private ImageView profileImage;
    private TextView sellerName;
    private RecyclerView recyclerView;
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileImage = findViewById(R.id.seller_profile_image);
        sellerName = findViewById(R.id.seller_name);
        recyclerView = findViewById(R.id.profile_recycler_view);
        backBtn = findViewById(R.id.profile_back_btn);
        mDisplayedSeller = Parcels.unwrap(getIntent().getParcelableExtra("User"));
        setUpProfilePage();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent();
                setResult(RESULT_OK, backIntent);
                finish();
            }
        });

    }

    private void setUpProfilePage() {
        sellerName.setText(mDisplayedSeller.getFirst() + " " + mDisplayedSeller.getLast());
    }

}
