package com.example.stephanieangulo.orlyst;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CameraFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "CameraFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Context mContext;
    private CameraView cameraView;
    private Button captureBtn;

    private DatabaseReference mUserReference;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    private boolean takingProfilePhoto;
    private boolean canTakePicture;
    private boolean photoUploaded = false;
    private boolean infoUploaded = false;

    public CameraFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CameraFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance(String param1, String param2) {
        CameraFragment fragment = new CameraFragment();
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
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        mContext = getContext();

        cameraView = view.findViewById(R.id.capture_camera_view);
        cameraView.addCameraKitListener(cameraListener);
        captureBtn = view.findViewById(R.id.capture_btn);
        takingProfilePhoto = TakePhotoActivity.isTakingProfilePhoto();

        mAuth = AppData.firebaseAuth;
        mUser = mAuth.getCurrentUser();
        mUserReference = AppData.firebaseDatabase.getReference("users").child(mUser.getUid());
        mStorage = AppData.firebaseStorage;
        storageReference = mStorage.getReference();

        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
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

    @Override
    public void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    public void onPause() {
        cameraView.stop();
        super.onPause();
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
    private void captureImage() {
        cameraView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
            @Override
            public void callback(CameraKitImage cameraKitImage) {
                cameraListener.onImage(cameraKitImage);
                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = ItemImage.scaleDownBitmap(bitmap);
                ItemImage image = new ItemImage(bitmap);
                if(takingProfilePhoto) {
                    addProfilePhotoToDB(image.convertToBytes(100));
                } else {
                    Intent intent = new Intent(mContext, PostItemActivity.class);
                    intent.putExtra("bytes", image.convertToBytes(100));
                    startActivity(intent);
                }
            }
        });
    }
    private CameraKitEventListener cameraListener = new CameraKitEventListener() {
        @Override
        public void onEvent(CameraKitEvent cameraKitEvent) {
            switch (cameraKitEvent.getType()) {
                case CameraKitEvent.TYPE_CAMERA_OPEN:
                    canTakePicture = true;
                    break;

                case CameraKitEvent.TYPE_CAMERA_CLOSE:
                    canTakePicture = false;
                    break;
            }
        }

        @Override
        public void onError(CameraKitError cameraKitError) {
            Toast.makeText(mContext, "Unable to take an image :(",
                    Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error: " + cameraKitError.toString());
        }

        @Override
        public void onImage(CameraKitImage cameraKitImage) {
            cameraKitImage.getBitmap();
        }

        @Override
        public void onVideo(CameraKitVideo cameraKitVideo) {

        }
    };
    private void addProfilePhotoToDB(byte[] profilePic) {
        String key = mUserReference.child("profilePicture").push().getKey();
        DatabaseReference profilePictureRef = mUserReference.child("profilePicture");

        uploadImage(profilePic, key);
        profilePictureRef.setValue(key).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "Successfully uploaded item info!");
                infoUploaded = true;
                returnToNewsFeed(photoUploaded);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "FAIL");
                Toast.makeText(mContext, "Cannot upload image at this time, please try again later",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e(TAG, "Complete");
            }
        });
    }
    private void uploadImage(byte[] image, String key) {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference ref = storageReference.child("images/"+ key);
        ref.putBytes(image)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        photoUploaded = true;
                        returnToNewsFeed(infoUploaded);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(mContext, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    }
                });
    }
    private void returnToNewsFeed(boolean success) {
        if(success) {
            Toast.makeText(mContext, "Uploaded", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
        }
    }

}
