package com.example.stephanieangulo.orlyst;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class AppData {

    private static final String GS_BUCKET = "gs://orlystapp.appspot.com/";
    private static final String USERS_ROOT = "users";
    private static final String ITEMS_ROOT = "items";
    protected static FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    protected static FirebaseStorage firebaseStorage = FirebaseStorage.getInstance(GS_BUCKET);
    protected static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    protected static DatabaseReference userRootReference = firebaseDatabase.getReference().child(USERS_ROOT);
    protected static DatabaseReference itemRootReference = firebaseDatabase.getReference().child(ITEMS_ROOT);
    protected static DatabaseReference getItemReference(String id) {
        return firebaseDatabase.getReference().child(USERS_ROOT).child(id).child(ITEMS_ROOT);
    }

}
