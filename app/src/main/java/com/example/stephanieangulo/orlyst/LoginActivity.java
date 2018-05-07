package com.example.stephanieangulo.orlyst;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
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
    private boolean invalidCreds = false;
    private boolean nonExistingUser = false;
    private android.support.v7.app.ActionBar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.DarkTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        mAuth = FirebaseAuth.getInstance();

        toolBar = getSupportActionBar();
        emailText = findViewById(R.id.email_text);
        passwordText = findViewById(R.id.password_text);
        loginBtn = findViewById(R.id.login_btn);
        signUpPageBtn = findViewById(R.id.sign_up_page_btn);

        toolBar.setTitle("");

        updateButtonStatus(false);
        addTextListeners();
        setTouchListeners(findViewById(R.id.login_view));
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
                        invalidCreds = false;
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
                            invalidCreds = true;
                            setupFloatingLabelErrors();
                        } else if(e instanceof FirebaseAuthInvalidUserException) {
                            showError();
                            nonExistingUser = true;
                            setupFloatingLabelErrors();
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
    private void setupFloatingLabelErrors() {
        final TextInputLayout floatingPasswordLabel = findViewById(R.id.user_password_text_input_layout);
        floatingPasswordLabel.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if(text.toString().length() > 0) {
                    invalidCreds = false;
                    nonExistingUser = false;
                    floatingPasswordLabel.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        final TextInputLayout floatingEmailLabel = findViewById(R.id.user_email_text_input_layout);
        floatingEmailLabel.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if(text.toString().length() > 0) {
                    invalidCreds = false;
                    nonExistingUser = false;
                    floatingPasswordLabel.setErrorEnabled(false);
                    floatingEmailLabel.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if(invalidCreds) {
            floatingPasswordLabel.setError(getString(R.string.invalid_creds));
            floatingPasswordLabel.setErrorEnabled(true);
        } else if(nonExistingUser) {
            floatingEmailLabel.setError(getString(R.string.user_doesnt_exists));
            floatingEmailLabel.setErrorEnabled(true);

        }
    }
    private void showError() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);

        emailText.startAnimation(shake);
        passwordText.startAnimation(shake);
    }
    public void setTouchListeners(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(view);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setTouchListeners(innerView);
            }
        }
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
