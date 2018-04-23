package com.example.stephanieangulo.orlyst;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class TakePhotoActivity extends AppCompatActivity
        implements GalleryFragment.OnFragmentInteractionListener,
        CameraFragment.OnFragmentInteractionListener {

    private static final String TAG = "TakePhotoActivity";
    private ActionBar toolbar;
    private Context mContext;
    Button cancelBtn;
    Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        mContext = this;
        cancelBtn = findViewById(R.id.cancel_btn);
        toolbar = getSupportActionBar();

        BottomNavigationView navigation = findViewById(R.id.camera_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        toolbar.setTitle("Camera");
        loadFragment(new CameraFragment());

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "returning back to main activity (news feed)");
                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
            }
        });

    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.camera_navigation_library:
                    toolbar.setTitle("Library");
                    fragment = new GalleryFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.camera_navigation_capture:
                    toolbar.setTitle("Capture");
                    fragment = new CameraFragment();
                    loadFragment(fragment);
                    return true;
            }
            fragment = new NewsFeedFragment();
            loadFragment(fragment);
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.create_item_frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }
}
