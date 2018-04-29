package com.example.stephanieangulo.orlyst;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

public class ItemDetailActivity extends AppCompatActivity {
    private static final String TAG = ItemDetailActivity.class.getSimpleName();
    private Context mContext;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private DatabaseReference itemRef;
    private Item displayedItem;
    private User userSeller;
    private TextView itemTitle;
    private TextView itemDescription;
    private TextView itemSeller;
    private ImageView itemImage;
    private ImageButton backBtn;
    private Button watchlistBtn;
    private Button contactBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_item);

        mContext = this;
        mAuth = AppData.firebaseAuth;
        mUser = mAuth.getCurrentUser();

        itemTitle = findViewById(R.id.detail_item_title);
        itemDescription = findViewById(R.id.item_description_tv);
        itemSeller = findViewById(R.id.seller_name_tv);
        itemImage = findViewById(R.id.detail_item_image);
        watchlistBtn = findViewById(R.id.detail_watchlist_btn);
        contactBtn = findViewById(R.id.detail_contact_btn);
        backBtn = findViewById(R.id.detail_back_btn);

        displayedItem = Parcels.unwrap(getIntent().getParcelableExtra("Item"));
        userSeller = Parcels.unwrap(getIntent().getParcelableExtra("User"));

        itemRef = AppData.firebaseDatabase.getReference("users")
                .child(userSeller.getUserID()).child("items").child(displayedItem.getKey());


        setUpDetailPage();
        onAddToWatchlist();
        onItemSellerName();

        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Need to implement add to contact seller function");

            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent();
                setResult(RESULT_OK, backIntent);
                finish();
            }
        });


    }
    private void setUpDetailPage() {
        Log.d(TAG, "User email is " + userSeller.getEmail());
        itemTitle.setText(displayedItem.getItemName());
        itemDescription.setText(displayedItem.getDescription());
        if(displayedItem.getBytes() != null)
            setItemImage(displayedItem.getBytes());
        itemSeller.setText("by " + displayedItem.getSeller());
        itemSeller.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
    }
    private void setItemImage(byte[] jpeg) {
        Glide.with(mContext)
                .load(jpeg)
                .asBitmap()
                .into(itemImage);

    }
    private void onAddToWatchlist() {
        watchlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference watchlistRef = AppData.firebaseDatabase.getReference("users")
                        .child(mUser.getUid()).child("watchlist").child(displayedItem.getKey());
                addItemToWatchlist(itemRef, watchlistRef);

            }
        });
    }
    private void onItemSellerName() {
        itemSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Seller clicked");

                Intent profileIntent = new Intent(mContext, ProfileActivity.class);
                profileIntent.putExtra("User", Parcels.wrap(userSeller));
                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, profileIntent, 0);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "profile");
                builder.setContentIntent(pendingIntent);
                startActivityForResult(profileIntent, 1);
            }
        });
    }
    private void addItemToWatchlist(DatabaseReference fromPath, final DatabaseReference toPath) {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if(databaseError != null) {
                            Log.e(TAG, databaseError.getMessage());
                        } else {
                            Log.d(TAG, "successfully added to watchlist!");
                            Toast.makeText(ItemDetailActivity.this,
                                    "Added this item to your list :)", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
