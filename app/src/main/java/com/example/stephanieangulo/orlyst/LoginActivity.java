package com.example.stephanieangulo.orlyst;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    // entry point of Firebase Auth SDK
    private FirebaseAuth mAuth;

    private EditText emailText;
    private EditText passwordText;
    private Button loginBtn;
    private Button signUpPageBtn;
    private Context mContext;

    private boolean isEmailFilled = false;
    private boolean isPasswordFilled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        mAuth = AppData.firebaseAuth;
        //mAuth.signOut(); //<-- how to sign out a user

        emailText = findViewById(R.id.email_text);
        passwordText = findViewById(R.id.password_text);
        loginBtn = findViewById(R.id.login_btn);
        signUpPageBtn = findViewById(R.id.sign_up_page_btn);

        updateButtonStatus(false);
        addTextListeners();

        signUpPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpPageIntent = new Intent(mContext, SignUpActivity.class);
                startActivity(signUpPageIntent);
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI();
    }

    private void updateUI() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // if firebase user exists, skip login page, take them to their news feed
            Log.d(TAG,"HELLO THERE " + user.getEmail());
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
        } else {
            // else no changes bc user is not signed in
            Log.d(TAG, "No user signed in");
        }
    }

    public void onLogin(View view) {
        // only let users click sign up when button status is enabled
        if(loginBtn.isEnabled()) {
            login();
        }
    }

    private void login() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        final Intent newsFeedIntent = new Intent(mContext, MainActivity.class);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(newsFeedIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            // TODO: make password box red when password incorrect? (firebaseAuthInvalidCredentialsException)
                            // TODO: allow user to request password change after 3 incorrect tries?
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "Login failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addTextListeners() {
        emailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isEmailFilled = s.length() != 0;

                updateButtonStatus(isEmailFilled && isPasswordFilled);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isEmailFilled = s.length() != 0;

                updateButtonStatus(isEmailFilled && isPasswordFilled);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passwordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isPasswordFilled = s.length() != 0;

                updateButtonStatus(isEmailFilled && isPasswordFilled);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isPasswordFilled = s.length() != 0;

                updateButtonStatus(isEmailFilled && isPasswordFilled);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void updateButtonStatus(boolean filled) {
        if(filled) {
            loginBtn.setClickable(true);
            loginBtn.setEnabled(true);
            loginBtn.setAlpha(1f);
        } else {
            loginBtn.setClickable(false);
            loginBtn.setEnabled(false);
            loginBtn.setAlpha(.5f);
        }
    }
}
