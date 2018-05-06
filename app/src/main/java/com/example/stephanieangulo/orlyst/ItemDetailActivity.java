package com.example.stephanieangulo.orlyst;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
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
    private static final int ITEM_DELETE_RESULT_CODE = 3;
    private static final String EDIT_TEXT = "EDIT YOUR ITEM";
    private static final String DELETE_TEXT = "DELETE YOUR ITEM";
    private Context mContext;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private DatabaseReference itemRef;
    private Item displayedItem;
    private User userSeller;
    private TextView itemTitle;
    private TextView itemDescription;
    private TextView itemSeller;
    private TextView itemCategory;
    private TextView itemPrice;
    private ImageView itemImage;
    private ImageButton backBtn;
    private Button topBtn;
    private Button bottomBtn;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_item);

        mContext = this;
        mAuth = AppData.firebaseAuth;
        mUser = mAuth.getCurrentUser();
        intent = getIntent();

        itemTitle = findViewById(R.id.detail_item_title);
        itemDescription = findViewById(R.id.item_description_tv);
        itemSeller = findViewById(R.id.seller_name_tv);
        itemImage = findViewById(R.id.detail_item_image);
        itemCategory = findViewById(R.id.this_item_category);
        itemPrice = findViewById(R.id.this_item_price);
        topBtn = findViewById(R.id.detail_watchlist_btn);
        bottomBtn = findViewById(R.id.detail_contact_btn);
        backBtn = findViewById(R.id.detail_back_btn);

        displayedItem = Parcels.unwrap(getIntent().getParcelableExtra("Item"));
        userSeller = Parcels.unwrap(getIntent().getParcelableExtra("User"));
        //fromNewsfeed = intent.getBooleanExtra("fromNewsfeed", false);
        //fromProfile = intent.getBooleanExtra("fromProfile", false);

        itemRef = AppData.firebaseDatabase.getReference("users")
                .child(userSeller.getUserID()).child("items").child(displayedItem.getKey());


        setUpDetailPage();
        updateButtons();
        onItemSellerName();


        backBtn.setOnClickListener(v -> {
            Intent backIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, backIntent);
            finish();
        });

    }

    private void updateButtons() {
        if(userSeller.getUserID().equals(mUser.getUid())) {
            setUpEditBtn();
            setUpDeleteButton();
        } else {
            setUpWatchlistBtn();
            setUpContactBtn();
        }
    }



    private void setUpEditBtn(){
        topBtn.setText(EDIT_TEXT);
        topBtn.setOnClickListener(v -> {
            Toast.makeText(mContext, "Edit function not working yet sorry :(", Toast.LENGTH_SHORT).show();
            Log.d("hi", "hi");
        });
    }

    private void setUpContactBtn(){
        bottomBtn.setOnClickListener(v -> {
            String[] emailAddress = new String[1];
            emailAddress[0] = displayedItem.getEmail();
            composeEmail(emailAddress, displayedItem.getSeller(), userSeller.getFirst(), displayedItem.getItemName());
        });
    }

    private void composeEmail(String[] address, String sellerName, String buyerName, String itemName){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, address);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hey " + sellerName + ", ");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[ORLYST] " + buyerName + " is contacting you about " + itemName);
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        }

    }

    private void setUpDetailPage() {
        Log.d(TAG, "User email is " + userSeller.getEmail());
        itemTitle.setText(displayedItem.getItemName());
        itemDescription.setText(displayedItem.getDescription());
        if(displayedItem.getBytes() != null)
            setItemImage(displayedItem.getBytes());
        itemSeller.setText("by " + displayedItem.getSeller());
        itemSeller.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        itemPrice.setText("$" + displayedItem.getPrice());
        itemCategory.setText(displayedItem.getCategory());

    }
    private void setItemImage(byte[] jpeg) {
        Glide.with(mContext)
                .load(jpeg)
                .asBitmap()
                .into(itemImage);

    }

    private void onItemSellerName() {
        itemSeller.setOnClickListener(v -> {
            Log.d(TAG, "Seller clicked");

            Intent profileIntent = new Intent(mContext, ProfileActivity.class);
            profileIntent.putExtra("User", Parcels.wrap(userSeller));
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, profileIntent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "profile");
            builder.setContentIntent(pendingIntent);
            startActivityForResult(profileIntent, 1);
        });
    }
    private void setUpWatchlistBtn() {
        topBtn.setOnClickListener(v -> {
            if(topBtn.getText().toString().equals(EDIT_TEXT)) {
                Log.d(TAG, "This is your item!");
            } else {
                DatabaseReference watchlistRef = AppData.firebaseDatabase.getReference("users")
                        .child(mUser.getUid()).child("watchlist").child(displayedItem.getKey());
                addItemToWatchlist(itemRef, watchlistRef);
            }
        });
    }

    private void setUpDeleteButton(){
        bottomBtn.setText(DELETE_TEXT);
        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent deleteItemIntent = new Intent(mContext, MainActivity.class);
                itemRef.removeValue();
                Toast.makeText(mContext, "Item deleted", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(deleteItemIntent);
            }
        });
    }

     private void addItemToWatchlist(DatabaseReference fromPath, final DatabaseReference toPath) {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue(), (databaseError, databaseReference) -> {
                    if(databaseError != null) {
                        Log.e(TAG, databaseError.getMessage());
                    } else {
                        Log.d(TAG, "successfully added to watchlist!");
                        Toast.makeText(ItemDetailActivity.this,
                                "Added this item to your list :)", Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
