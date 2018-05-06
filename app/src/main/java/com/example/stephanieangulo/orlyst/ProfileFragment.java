package com.example.stephanieangulo.orlyst;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class ProfileFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private  static  final String TAG = "ProfileFragment";
    private static final int FROM_PROFILE_RESULT_CODE = 234;

    private FirebaseAuth mAuth;
    private User mUser = new User();
    private FirebaseUser currentUser;

    private Context mContext;
    private RecyclerView recyclerView;
    private UserListAdapter mAdapter;
    private TextView mUsername;
    private Button userItemsButton;
    private Button watchlistButton;

    private List<Item> mItems = new ArrayList<>();
    private List<Item> mWatchlist = new ArrayList<>();
    private List<User> mUsers = new ArrayList<>();
    private List<Item> itemsDisplay = new ArrayList<>();
    private Map<String, Item> watchlistCurrentUser = new HashMap<>();
    private boolean onUserItems = true;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_action_bar, menu);  // Use filter.xml from step 1
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.edit_profile){
            Toast.makeText(mContext,"Lol you can't edit your profile yet sorry", Toast.LENGTH_SHORT).show();
            return true;
        } else if(id == R.id.settings) {
            Toast.makeText(mContext, "Lol you can't go to your settings yet sorry", Toast.LENGTH_SHORT).show();
        } else if(id == R.id.log_out) {
            mAuth.signOut();
            Intent loginIntent = new Intent(mContext, LoginActivity.class);
            startActivity(loginIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mUsers = fetchUsers();


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userItemsButton = view.findViewById(R.id.user_items_btn);
        watchlistButton = view.findViewById(R.id.watchlist_btn);
        recyclerView = view.findViewById(R.id.f_profile_recycler_view);

        mContext = getContext();
        mUsername = view.findViewById(R.id.your_username);
        mAdapter = new UserListAdapter(mContext, mItems);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        userItemsButton.setOnClickListener(this);
        watchlistButton.setOnClickListener(this);

        fetchUser();
        setRecyclerView();
        onProfileItemClick();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.user_items_btn:
                onUserItems = true;
                updateRecyclerView();
                break;
            case R.id.watchlist_btn:
                onUserItems = false;
                updateRecyclerView();
        }
    }

    private void fetchUser() {
        final DatabaseReference userRef = AppData.firebaseDatabase.getReference("users")
                .child(mAuth.getCurrentUser().getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User foundUser = dataSnapshot.getValue(User.class);

                Log.d(TAG, "User found --> " + foundUser.getFirst());
                mItems = new ArrayList<>(foundUser.getItems().values());
                mWatchlist = new ArrayList<>(foundUser.getWatchlist().values());
                mUser = foundUser;
                Log.d(TAG, "User has " + mItems.size() + " items");
                Log.d(TAG, "User has " + mWatchlist.size() + " itemson their watchlist");
                mItems.sort(Comparator.comparing(Item::getTimestamp));
                mWatchlist.sort(Comparator.comparing(Item::getTimestamp));

                Collections.reverse(mItems);
                Collections.reverse(mWatchlist);
                mUsername.setText(foundUser.getFirst() + " " + foundUser.getLast());

                updateRecyclerView();
                Log.d(TAG,"Adapter count = " +mAdapter.getItemCount());
                Log.d(TAG, "Adapter? " + mAdapter + " " + (mAdapter == null));
                fetchDisplayedImages(true);


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getDetails());
            }
        });
    }
    private void fetchDisplayedImages(boolean onUserItems) {
        if(onUserItems) {
            Log.d(TAG, "Fetching user items' images");
            for(Item item: mItems)
                fetchImage(item);
        } else {
            Log.d(TAG, "Fetching user watchlist' images");
            for(Item item: mWatchlist)
                fetchImage(item);
        }
    }
    private void fetchImage(Item item) {
        final Item test = item;
        StorageReference storageRef = AppData.firebaseStorage.getReference();
        StorageReference pathRef = storageRef.child("images/");
        StorageReference imageRef = pathRef.child(item.getKey());
        final long LIMIT = 512 * 512;
        imageRef.getBytes(LIMIT).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                test.setBytes(bytes);
                mAdapter.notifyDataSetChanged();
                Log.d(TAG, "successfully got the image!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    private void setRecyclerView(){
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);
    }
    private void updateRecyclerView() {
        if(onUserItems) {
            itemsDisplay = mItems;
            mAdapter = new UserListAdapter(mContext, itemsDisplay);
            fetchDisplayedImages(true);
        } else {
            itemsDisplay = mWatchlist;
            mAdapter = new UserListAdapter(mContext, itemsDisplay);
            fetchDisplayedImages(false);
        }

        setRecyclerView();
        mAdapter.notifyDataSetChanged();
    }

    private void onProfileItemClick() {
        recyclerView.addOnItemTouchListener(new MyRecyclerItemClickListener(getActivity(),
                (view, position) -> {
                    Item selectedItem = itemsDisplay.get(position);
                    String sellerID = selectedItem.getSellerID();
                    User selectedUser = null;
                    User userCurrent = null;
                    for(User user: mUsers) {
                        if(user.getUserID().equals(currentUser.getUid()))
                            userCurrent = user;
                        if(user.getUserID().equals(sellerID))
                            selectedUser = user;
                        if(selectedUser != null && userCurrent != null)
                            break;
                    }
                    watchlistCurrentUser = userCurrent.getWatchlist();
                    Intent itemIntent = new Intent(getActivity(), ItemDetailActivity.class);
                    itemIntent.putExtra("Item", Parcels.wrap(selectedItem));
                    itemIntent.putExtra("userSeller", Parcels.wrap(selectedUser));
                    itemIntent.putExtra("watchlistCurrentUser", Parcels.wrap(watchlistCurrentUser));
                    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, itemIntent, 0);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "profile");
                    builder.setContentIntent(pendingIntent);
                    startActivityForResult(itemIntent, FROM_PROFILE_RESULT_CODE);
                }));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == FROM_PROFILE_RESULT_CODE) {
            if(resultCode == Activity.RESULT_OK){
                Intent refreshIntent = new Intent(mContext, MainActivity.class);
                refreshIntent.putExtra("RESULT_CODE", FROM_PROFILE_RESULT_CODE);
                startActivity(refreshIntent);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private List<User> fetchUsers() {
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
                    users.add(user);
                    Log.d(TAG, "item list size " + user.getItems().values().size());
                    for(Item item: user.getItems().values()) {
                        List<String> keys = getAllItemKeys(mItems);
                        if(!keys.contains(item.getKey())) {
                            Log.d(TAG, "getting item " + item.getItemName());
                            mItems.add(item);
                            Log.d(TAG, "Time stamp = " + item.getTimestamp());
                            // TODO: add booleans on whether or not item is in watchlist
                            fetchImage(item);
                        }
                    }
                    Log.d(TAG, "hello " + user.getFirst());
                }
                mItems.sort(Comparator.comparing(Item::getTimestamp));
                Collections.reverse(mItems);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getDetails());
            }
        });
        return users;
    }

    private List<String> getAllItemKeys(List<Item> items) {
        List<String> keys = new ArrayList<>();
        for(Item item: items) {
            keys.add(item.getKey());
        }
        return keys;
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
