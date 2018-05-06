package com.example.stephanieangulo.orlyst;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

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
 * {@link NewsFeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewsFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFeedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int FROM_NEWSFEED_RESULT_CODE = 123;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static final String TAG = "NewsFeedFragment";

    private RecyclerView recyclerView;

    private Context mContext;
    private NewsFeedAdapter mAdapter;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private List<Item> mItems = new ArrayList<>();
    private List<User> mUsers = new ArrayList<>();
    private Map<String, Item> watchlistCurrentUser = new HashMap<>();
    private FloatingActionButton addBtn;


    public NewsFeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewsFeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsFeedFragment newInstance(String param1, String param2) {
        NewsFeedFragment fragment = new NewsFeedFragment();
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
        inflater.inflate(R.menu.news_feed_search, menu);
        SearchManager searchManager =
                (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
        mContext = getContext();
        mAuth = AppData.firebaseAuth;

        recyclerView = view.findViewById(R.id.feed_recycler_view);
        addBtn = view.findViewById(R.id.fabAdd);
        mUsers = fetchUsers();
        currentUser = mAuth.getCurrentUser();


        mAdapter = new NewsFeedAdapter(getActivity(), mItems);

        setUpRecyclerView();
        onNewsFeedItemClick();
        onAddButtonClick();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onStop() {
        super.onStop();
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

    private void setUpRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    private void onNewsFeedItemClick() {
        recyclerView.addOnItemTouchListener(new MyRecyclerItemClickListener(getActivity(),
                (view, position) -> {
                    Item selectedItem = mItems.get(position);
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
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "newsfeed");
                    builder.setContentIntent(pendingIntent);
                    startActivityForResult(itemIntent, FROM_NEWSFEED_RESULT_CODE);
                }));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == FROM_NEWSFEED_RESULT_CODE) {
            if(resultCode == Activity.RESULT_OK){
                Intent refreshIntent = new Intent(mContext, MainActivity.class);
                startActivity(refreshIntent);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }


    private void onAddButtonClick() {
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "FAB clicked!");
                Intent intent = new Intent(mContext, TakePhotoActivity.class);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            }
        });
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
    private void fetchImage(Item item) {
        final Item itemWithImage = item;
        StorageReference storageRef = AppData.firebaseStorage.getReference();
        StorageReference pathRef = storageRef.child("images/");
        StorageReference imageRef = pathRef.child(item.getKey());
        final long LIMIT = 512 * 512;
        imageRef.getBytes(LIMIT).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                itemWithImage.setBytes(bytes);
                mAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    private List<String> getAllItemKeys(List<Item> items) {
        List<String> keys = new ArrayList<>();
        for(Item item: items) {
            keys.add(item.getKey());
        }
        return keys;
    }


}
