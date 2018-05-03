package com.example.stephanieangulo.orlyst;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


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

    private FirebaseAuth mAuth;
    private User mUser = new User();

    private Context mContext;
    private RecyclerView recyclerView;
    private UserListAdapter mAdapter;
    private TextView mUsername;
    private Button userItemsButton;
    private Button watchlistButton;

    private List<Item> mItems = new ArrayList<>();
    private List<Item> mWatchlist = new ArrayList<>();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userItemsButton = view.findViewById(R.id.user_items_btn);
        watchlistButton = view.findViewById(R.id.watchlist_btn);
        recyclerView = view.findViewById(R.id.f_profile_recycler_view);

        mContext = getContext();
        mUsername = view.findViewById(R.id.your_username);
        mAdapter = new UserListAdapter(mContext, mItems);
        mAuth = FirebaseAuth.getInstance();


        userItemsButton.setOnClickListener(this);
        watchlistButton.setOnClickListener(this);

        fetchUser();
        setRecyclerView();

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
            mAdapter = new UserListAdapter(mContext, mItems);
            fetchDisplayedImages(true);
        } else {
            mAdapter = new UserListAdapter(mContext, mWatchlist);
            fetchDisplayedImages(false);
        }

        setRecyclerView();
        mAdapter.notifyDataSetChanged();
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
