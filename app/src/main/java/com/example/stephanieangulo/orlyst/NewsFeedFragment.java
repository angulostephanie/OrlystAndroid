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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.List;


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
        mItems = fetchAllItems();

        recyclerView = view.findViewById(R.id.feed_recycler_view);
        addBtn = view.findViewById(R.id.fabAdd);
        currentUser = mAuth.getCurrentUser();

        mAdapter = new NewsFeedAdapter(mContext, mItems);

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

    private void updateRecyclerView() {
        mAdapter = new NewsFeedAdapter(mContext, mItems);
        setUpRecyclerView();
        mAdapter.notifyDataSetChanged();
    }

    private void onNewsFeedItemClick() {
        recyclerView.addOnItemTouchListener(new MyRecyclerItemClickListener(getActivity(),
                (view, position) -> {
                    Item selectedItem = mItems.get(position);
                    Log.d(TAG, "clicked " + selectedItem.getItemName());
                    String sellerID = selectedItem.getSellerID();
                    fetchSeller(selectedItem, sellerID);
                }));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == FROM_NEWSFEED_RESULT_CODE) {
            if(resultCode == Activity.RESULT_OK){
                Intent refreshIntent = new Intent(mContext, MainActivity.class);
                startActivity(refreshIntent);
                mAdapter.notifyDataSetChanged();
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


    private List<Item> fetchAllItems() {
        final List<Item> items = new ArrayList<>();
        final DatabaseReference itemRef = AppData.firebaseDatabase.getReference("items");
        itemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for (DataSnapshot data : dataSnapshots) {
                    String key = data.getKey();
                    DataSnapshot a = dataSnapshot.child(key);
                    Item item = a.getValue(Item.class);
                    items.add(item);
                    fetchImage(item);
                    Log.d(TAG, "Found this item " + item.getItemName());
                }
                mItems = new ArrayList<>(items);
                Log.d(TAG, "mitems size --> " + mItems.size());
                mItems.sort(Comparator.comparing(Item::getTimestamp));
                Collections.reverse(mItems);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return mItems;
    }

    private void fetchSeller(Item selectedItem, String sellerID) {
        final DatabaseReference userRef = AppData.firebaseDatabase.getReference("users").child(sellerID);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User seller = dataSnapshot.getValue(User.class);
                String currentID = currentUser.getUid();
                Log.d(TAG, "Seller's email =" + seller.getEmail());
                fetchYourWatchlist(selectedItem, seller, currentID);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getDetails());
            }
        });
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
                Log.d(TAG, "Got image! ");
                itemWithImage.setBytes(bytes);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Fetching image failed :/ " + e.getMessage());
                itemWithImage.setImageFound(false);
            }
        }).addOnCompleteListener(new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                Log.d(TAG, "Notifying adapter of data changes! ");
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void fetchYourWatchlist(Item selectedItem, User seller, String currentID) {
        final List<String> yourWatchlist = new ArrayList<>();
        final DatabaseReference watchlistRef = AppData.watchlistRootReference.child(currentID);
        watchlistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for (DataSnapshot data : dataSnapshots) {
                    String itemID = (String)data.getValue();
                    yourWatchlist.add(itemID);
                }
                goToDetailActivity(selectedItem, seller, yourWatchlist);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void goToDetailActivity(Item selectedItem, User seller, List<String> yourWatchList) {
        Intent itemIntent = new Intent(getActivity(), ItemDetailActivity.class);
        itemIntent.putExtra("selectedItem", Parcels.wrap(selectedItem));
        itemIntent.putExtra("seller", Parcels.wrap(seller));
        itemIntent.putExtra("yourWatchList", Parcels.wrap(yourWatchList));
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, itemIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "newsfeed");
        builder.setContentIntent(pendingIntent);
        startActivityForResult(itemIntent, FROM_NEWSFEED_RESULT_CODE);
    }

}
