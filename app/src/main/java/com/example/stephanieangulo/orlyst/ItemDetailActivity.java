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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ItemDetailActivity extends AppCompatActivity {
    private static final String TAG = ItemDetailActivity.class.getSimpleName();
    private static final int ITEM_DELETE_RESULT_CODE = 3;
    private static final String EDIT_TEXT = "EDIT YOUR ITEM";
    private static final String DELETE_TEXT = "DELETE YOUR ITEM";
    private Context mContext;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private DatabaseReference itemRef;
    private DatabaseReference watchlistRef;
    private DatabaseReference peopleWatchingRef;
    private StorageReference imageRef;
    private Item displayedItem;
    private User userSeller;
    private Map<String, Item> watchlistCurrentUser = new HashMap<>();
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
    private Intent intent;
    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_item);

        mContext = this;
        mAuth = AppData.firebaseAuth;
        mUser = mAuth.getCurrentUser();
        intent = getIntent();

        toolbar = getSupportActionBar();
        toolbar.setTitle("");

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
        userSeller = Parcels.unwrap(getIntent().getParcelableExtra("userSeller"));
        watchlistCurrentUser = Parcels.unwrap(getIntent().getParcelableExtra("watchlistCurrentUser"));

        //fromNewsfeed = intent.getBooleanExtra("fromNewsfeed", false);
        //fromProfile = intent.getBooleanExtra("fromProfile", false);

        itemRef = AppData.firebaseDatabase.getReference("users")
                .child(userSeller.getUserID()).child("items").child(displayedItem.getKey());
        watchlistRef = AppData.firebaseDatabase.getReference("users")
                .child(mUser.getUid()).child("watchlist").child(displayedItem.getKey());
        peopleWatchingRef = AppData.firebaseDatabase.getReference("users")
                .child(userSeller.getUserID()).child("items").child(displayedItem.getKey()).child("peopleWatching");
        imageRef = AppData.firebaseStorage.getReference().child("images/").child(displayedItem.getKey());


        toolbar.setLogo(R.drawable.small_orlyst_logo);
        toolbar.setDisplayUseLogoEnabled(true);
        toolbar.setDisplayShowHomeEnabled(true);

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
            profileIntent.putExtra("User", Parcels.wrap(userSeller));
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, profileIntent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "profile");
            builder.setContentIntent(pendingIntent);
            startActivityForResult(profileIntent, 1);
        });
    }
    private void setUpWatchlistBtn() {
            if(watchlistCurrentUser.containsKey(displayedItem.getKey())) {
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
                addItemToWatchlist(itemRef, watchlistRef);
                addPeopleWatchingToItem(mUser.getUid());
                //addItemToPeopleWatching(itemRef, peopleWatchingRef);
                setUpRemoveWatchListBtn();
            }
        });
    }
    private void setUpRemoveWatchListBtn(){
        DatabaseReference userWatchingRef  = AppData.firebaseDatabase.getReference("users")
                .child(userSeller.getUserID()).child("items").child(displayedItem.getKey()).child("peopleWatching").child(mUser.getUid());
        topBtn.setText("REMOVE FROM WATCHLIST");
        topBtn.setOnClickListener(null);
        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                watchlistRef.removeValue();
                userWatchingRef.removeValue();
                setUpAddWatchListBtn();
            }
        });
    }
    private void setUpDeleteButton(){
        bottomBtn.setText(DELETE_TEXT);
        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent deleteItemIntent = new Intent();

                removeItemFromWatchLists();
                itemRef.removeValue();
                deleteImage();

                Toast.makeText(mContext, "Item deleted", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK, deleteItemIntent);
                finish();
                //startActivity(deleteItemIntent);
            }
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
    private void removeItemFromWatchLists(){
                List<String> peopleWatching = fetchPeopleWatching();
                List<User> peopleWatchingUsers = fetchUsers(peopleWatching);

                for(User user : peopleWatchingUsers) {
                    user.getWatchlist().remove(displayedItem.getKey());
                }
    }

    private List<String> fetchPeopleWatching() {
        final List<String> users = new ArrayList<>();

        peopleWatchingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d(TAG, "yeeeeeeeet");
                for (DataSnapshot data : snapshot.getChildren()) {
                    String key = data.getKey();
                    DataSnapshot a = snapshot.child(key);
                    String userID = (String) a.getValue();
                    users.add(userID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return users;
    }

    private List<User> fetchUsers(List<String> usersIDToFetch) {
        final List<User> users = new ArrayList<>();
        final DatabaseReference userRef = AppData.firebaseDatabase.getReference("users");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for (DataSnapshot data : dataSnapshots) {
                    String key = data.getKey();
                    DataSnapshot a = dataSnapshot.child(key);
                    User user = a.getValue(User.class);
                    userDatabase.put(key, user);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getDetails());
            }
        });

        Iterator<String> iterator = userDatabase.keySet().iterator();

        for (String userID : usersIDToFetch){
            while (iterator.hasNext()) {
                iterator.next();
                if(userDatabase.containsKey(userID))
                    users.add(userDatabase.get(userID));
            }
        }

        return users;
    }



    private void addPeopleWatchingToItem(String userID) {
        DatabaseReference postItemRef = peopleWatchingRef.child(userID); //mItemReference.push();
        postItemRef.setValue(userID);


       /* postItemRef.updateChildren(itemValues).
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
        */
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
