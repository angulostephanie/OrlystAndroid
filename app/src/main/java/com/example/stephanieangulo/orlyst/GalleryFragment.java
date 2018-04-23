package com.example.stephanieangulo.orlyst;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GalleryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GalleryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "GalleryFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private GalleryAdapter mAdapter;
    private Context mContext;
    private Activity mActivity;
    private RecyclerView recyclerView;
    private ImageView selectedImage;
    private String mostRecent = "";
    private Button nextBtn;

    private List<byte[]> images;
    private final String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";
    private final String[] basicProjection = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media._ID
    };
    private final String[] recentProjection = {
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.MIME_TYPE
    };
    private final Uri imageGalleryLink = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    /*
    private static final int LIMIT = 8;
    private static final int PAGE_START = 1;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;
    */

    public GalleryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GalleryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GalleryFragment newInstance(String param1, String param2) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        mContext = getContext();
        mActivity = getActivity();
        mAdapter = new GalleryAdapter(mContext);

        recyclerView = view.findViewById(R.id.gallery_recycler_view);
        selectedImage = view.findViewById(R.id.selected_image_view);
        nextBtn = view.findViewById(R.id.next_btn);

        int permissionCheck = ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.READ_EXTERNAL_STORAGE);

        while(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            // TODO: I want to force the user from the beginning however, maybe when they login, idk.
        }

        if(!isGalleryEmpty()) {
            mostRecent = getMostRecentImage();
            ItemImage firstImage = new ItemImage(mostRecent);
            Bitmap bitmap = firstImage.decodeToBitmap();
            setSelectedImage(bitmap);
            mAdapter.setMovies(ItemImage.getAllShownImagePaths(mContext, imageGalleryLink, basicProjection, orderBy));
        }

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 4);
        RecyclerView.LayoutManager mLayoutManager = gridLayoutManager;
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new GalleryItemDecoration(3, 1, true, getContext()));
        recyclerView.setNestedScrollingEnabled(false);

        /*
        recyclerView.addOnScrollListener(new PaginationScrollListener(gridLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                //loadNextPage();
            }
            @Override
            public int getTotalPageCount() {
                int totalPages = getGallerySize()/LIMIT;
                if(getGallerySize()%LIMIT != 0)
                    totalPages+=1;
                return totalPages;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        */

        recyclerView.addOnItemTouchListener(new MyRecyclerItemClickListener(mContext,
                new MyRecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                images = GalleryAdapter.getImageGallery();
                ItemImage image = new ItemImage(images.get(position));
                Bitmap bitmap = image.decodeToBitmap();
                setSelectedImage(bitmap);

                Log.d(TAG, "Clicking on this image path" + position);
                Log.d(TAG, "First visible item position " + gridLayoutManager.findFirstVisibleItemPosition());

                Log.d(TAG, "Last visible item position " + gridLayoutManager.findLastVisibleItemPosition());
            }
        }));

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isGalleryEmpty()) {
                    Toast.makeText(mContext, "Image gallery is empty, please take a photo!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Bitmap originalBitmap = (((BitmapDrawable)selectedImage.getDrawable()).getBitmap());
                Bitmap bitmap = ItemImage.scaleDownBitmap(originalBitmap);
                ItemImage image = new ItemImage(bitmap);
                byte[] jpeg = image.convertToBytes(100);
                Intent intent = new Intent(mContext, PostItemActivity.class);
                intent.putExtra("bytes", jpeg);
                startActivity(intent);
            }
        });
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

    private void setSelectedImage(Bitmap bitmap) {
        ItemImage image = new ItemImage(bitmap);
        byte[] bytes = image.convertToBytes(100);
        Glide.with(mContext)
                .asBitmap()
                .load(bytes)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.spinner)
                        .fitCenter())
                .into(selectedImage);
    }

    private String getMostRecentImage() {
        // queries for first image in cursor
        // returns first image
        final Cursor cursor = createCursor(recentProjection, orderBy);
        if (cursor.moveToFirst()) {
            String imageLocation = cursor.getString(1);
            File imageFile = new File(imageLocation);
            cursor.close();
            if (imageFile.exists())
                return imageLocation;

        }
        return null;
    }

    private int getGallerySize() {
        Cursor cursor = createCursor(basicProjection, orderBy);
        int size = cursor.getCount();
        cursor.close();
        return size;
    }
    private boolean isGalleryEmpty() {
        return getGallerySize() == 0;
    }

    private Cursor createCursor(String[] projection, String orderBy) {
        // The cursor class allows us to query databases
        // This is needed to query through the device's image gallery
        ContentResolver imageGallery = mContext.getContentResolver();
        Cursor cursor = imageGallery.query(imageGalleryLink,
                projection, null, null, orderBy);
        return cursor;
    }

}
