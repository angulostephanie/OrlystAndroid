package com.example.stephanieangulo.orlyst;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

@SuppressLint("ClickableViewAccessibility")
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

        emailText = findViewById(R.id.email_text);
        passwordText = findViewById(R.id.password_text);
        loginBtn = findViewById(R.id.login_btn);
        signUpPageBtn = findViewById(R.id.sign_up_page_btn);

        updateButtonStatus(false);
        addTextListeners();
        addFocusListeners();

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
    public void onLogin(View view) {
        // only let users click sign up when button status is enabled
        if(loginBtn.isEnabled()) {
            login();
        }
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
    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private void login() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        final Intent newsFeedIntent = new Intent(mContext, MainActivity.class);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = authResult.getUser();
                        Log.d(TAG, "signInWithEmail:success, welcome " + user.getEmail());
                        startActivity(newsFeedIntent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof FirebaseAuthInvalidCredentialsException) {
                            Log.d(TAG, "YEEEEEEEET");
                            showError();
                        }
                        Log.d(TAG, "Error " + e.getClass());
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
    private void addFocusListeners() {
        emailText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.d(TAG, "Email has NO focus");
                    hideKeyboard(v);
                } else {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams
                            .SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                    Log.d(TAG, "Email does have focus");
                }
            }
        });
        passwordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.d(TAG, "Password has NO focus");
                    hideKeyboard(v);
                } else {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams
                            .SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                    Log.d(TAG, "Password does have focus");
                }
            }
        });
    }
    private void showError() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        emailText.startAnimation(shake);
        passwordText.startAnimation(shake);
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
