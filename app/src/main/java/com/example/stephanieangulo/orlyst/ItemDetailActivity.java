package com.example.stephanieangulo.orlyst;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemDetailActivity extends AppCompatActivity {
    private static final String TAG = ItemDetailActivity.class.getSimpleName();
    private static final int ITEM_DELETE_RESULT_CODE = 3;
    private static final String EDIT_TEXT = "EDIT YOUR ITEM";
    private static final String DELETE_TEXT = "DELETE YOUR ITEM";
    private Context mContext;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private DatabaseReference itemRef;
    private DatabaseReference sellerListRef;
    private DatabaseReference yourWatchlistRef;
    private DatabaseReference peopleWatchingRef;
    private StorageReference imageRef;
    private Item displayedItem;
    private User seller;
    private List<String> yourWatchlist = new ArrayList<>();
    private Map<String, User> userDatabase = new HashMap<>();
    private List<Item> mItems = new ArrayList<>();
    private List<User> peopleWatchingUsers;
    private TextView itemTitle;
    private TextView itemDescription;
    private TextView itemSeller;
    private TextView itemCategory;
    private TextView itemPrice;
    private ImageView itemImage;
    private ImageButton backBtn;
    private Button topBtn;
    private Button bottomBtn;
    private ActionBar toolbar;

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
        itemCategory = findViewById(R.id.this_item_category);
        itemPrice = findViewById(R.id.this_item_price);
        topBtn = findViewById(R.id.detail_watchlist_btn);
        bottomBtn = findViewById(R.id.detail_contact_btn);
        backBtn = findViewById(R.id.detail_back_btn);

        displayedItem = Parcels.unwrap(getIntent().getParcelableExtra("selectedItem"));
        seller = Parcels.unwrap(getIntent().getParcelableExtra("seller"));
        yourWatchlist = Parcels.unwrap(getIntent().getParcelableExtra("yourWatchList"));

        itemRef = AppData.itemRootReference.child(displayedItem.getKey());
        sellerListRef = AppData.userRootReference.child(seller.getUserID())
                .child("items").child(displayedItem.getKey());
        yourWatchlistRef = AppData.watchlistRootReference.child(mUser.getUid());
        peopleWatchingRef = AppData.itemRootReference.child(displayedItem.getKey()).child("peopleWatching");
        imageRef = AppData.firebaseStorage.getReference().child("images/").child(displayedItem.getKey());

        toolbar = getSupportActionBar();
        toolbar.setTitle("");
        toolbar.setLogo(R.drawable.small_orlyst_logo);
        toolbar.setDisplayUseLogoEnabled(true);
        toolbar.setDisplayShowHomeEnabled(true);

        setUpDetailPage();
        setUpButtons();
        onItemSellerName();

        backBtn.setOnClickListener(v -> {
            Intent backIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, backIntent);
            finish();
        });

    }

    private void setUpButtons() {
        if(seller.getUserID().equals(mUser.getUid())) {
            setUpEditBtn();
            setUpDeleteButton();
        } else {
            setUpWatchlistBtn();
            setUpContactBtn();
        }
    }

    private void setUpEditBtn() {
        topBtn.setText(EDIT_TEXT);
        topBtn.setOnClickListener(v -> {
            Toast.makeText(mContext, "Edit function not working yet sorry :(", Toast.LENGTH_SHORT).show();
            Log.d("hi", "hi");
        });
    }

    private void setUpContactBtn() {
        bottomBtn.setOnClickListener(v -> {
            String[] emailAddress = new String[1];
            emailAddress[0] = displayedItem.getEmail();
            composeEmail(emailAddress, displayedItem.getSeller(), seller.getFirst(), displayedItem.getItemName());
        });
    }

    private void setUpWatchlistBtn() {
        Set<String> watchListSet = new HashSet<>(yourWatchlist);
        if (watchListSet.contains(displayedItem.getKey())) {
            setUpRemoveWatchListBtn();
        } else {
            setUpAddWatchListBtn();
        }
    }

    private void setUpAddWatchListBtn(){
        topBtn.setText("ADD TO WATCHLIST");
        topBtn.setOnClickListener(null);
        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToYourWatchlist(displayedItem.getKey(), yourWatchlistRef);
                addPeopleWatchingToItem(mUser.getUid());
            }
        });
    }

    private void setUpRemoveWatchListBtn(){
        topBtn.setText("REMOVE FROM WATCHLIST");
        topBtn.setOnClickListener(null);
        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yourWatchlistRef.child(displayedItem.getKey()).removeValue();
                peopleWatchingRef.child(mUser.getUid()).removeValue();
                setUpAddWatchListBtn();
            }
        });
    }

    private void setUpDeleteButton(){
        bottomBtn.setText(DELETE_TEXT);
        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchPeopleWatching();
            }
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
        Log.d(TAG, "User email is " + seller.getEmail());
        itemTitle.setText(displayedItem.getItemName());
        itemDescription.setText(displayedItem.getDescription());
        if(displayedItem.getBytes() != null)
            setItemImage(displayedItem.getBytes());
        if(!displayedItem.isImageFound()) {
            itemImage.setImageResource(R.drawable.image_not_found);
        }
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
            profileIntent.putExtra("User", Parcels.wrap(seller));
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, profileIntent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "profile");
            builder.setContentIntent(pendingIntent);
            startActivityForResult(profileIntent, 1);
        });
    }

    private void deleteImage(){
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Toast.makeText(mContext, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPeopleWatching() {
        final List<String> userIDs = new ArrayList<>();

        peopleWatchingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "yeeeeeeeet");
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String key = data.getKey();
                    DataSnapshot a = dataSnapshot.child(key);
                    String userID = (String) a.getValue();
                    userIDs.add(userID);
                }
                 removeItemFromEveryWhere(userIDs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void removeItemFromEveryWhere(List<String> allPeopleWatching) {
        for(String someonesWatchlist: allPeopleWatching) {
            final DatabaseReference itemOnSomeonesWatchlist = AppData.firebaseDatabase
                    .getReference("watchlist").child(someonesWatchlist).child(displayedItem.getKey());
            itemOnSomeonesWatchlist.removeValue();
        }
        sellerListRef.removeValue();
        itemRef.removeValue();
        deleteImage();
        Toast.makeText(mContext, "Item deleted", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Deleted! closing activity :P");
        setResult(RESULT_OK);
        finish();
    }

    private void addPeopleWatchingToItem(String userID) {
        DatabaseReference thisPersonRef = peopleWatchingRef.child(userID);
        thisPersonRef.setValue(userID);
    }

    private void addItemToYourWatchlist(String itemKey, final DatabaseReference yourWatchlist) {
        yourWatchlist.child(displayedItem.getKey()).setValue(itemKey)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Successfully added this item to your watchlist :)");
                        setUpRemoveWatchListBtn();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, e.getMessage());
            }
        });
    }
}
