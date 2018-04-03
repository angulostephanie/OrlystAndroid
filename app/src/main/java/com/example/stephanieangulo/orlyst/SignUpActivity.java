package com.example.stephanieangulo.orlyst;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText firstNameText;
    private EditText lastNameText;
    private EditText emailText;
    private EditText passwordText;
    private Button signUpBtn;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);
        mContext = this;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        firstNameText = findViewById(R.id.first_name_text);
        lastNameText = findViewById(R.id.last_name_text);
        emailText = findViewById(R.id.new_email_text);
        passwordText = findViewById(R.id.new_password_text);
        signUpBtn = findViewById(R.id.sign_up_btn);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String firstName = firstNameText.getText().toString();
                final String lastName = lastNameText.getText().toString();
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();

                if(!isCompletelyFilled(firstName,lastName,email,password))
                    Toast.makeText(mContext, "PLEASE FILL OUT EVERYTHING", Toast.LENGTH_SHORT).show();
                else {
                    final Intent newsFeedIntent = new Intent(mContext, MainActivity.class);
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                writeNewUser(user, firstName, lastName);
                                startActivity(newsFeedIntent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(mContext, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void writeNewUser(FirebaseUser user, String first, String last) {
        User newUser = new User(first, last, user.getEmail(), user.getUid());
        UserProfileChangeRequest newProfile = new UserProfileChangeRequest.Builder()
                .setDisplayName(first + " " + last)
                .build();
        user.updateProfile(newProfile)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile created.");
                        } else {
                            Log.d(TAG, "Not created.");
                        }
                    }
                });
        mDatabase.child("users").child(user.getUid()).setValue(newUser);
    }

    private boolean isCompletelyFilled(String first, String last, String email, String password) {
        return !TextUtils.isEmpty(first) && !TextUtils.isEmpty(last) && !TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password);
    }
}
