package com.example.stephanieangulo.orlyst;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
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
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private static final String OXY_EMAIL = "@oxy.edu";
    // entry point of Firebase Auth SDK
    private FirebaseAuth mAuth;

    private EditText firstNameText;
    private EditText lastNameText;
    private EditText emailText;
    private EditText passwordText;
    private Button signUpBtn;
    private Button backBtn;
    private Context mContext;

    private boolean isFirstFilled = false;
    private boolean isLastFilled = false;
    private boolean isEmailFilled = false;
    private boolean isPasswordFilled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);
        mContext = this;
        mAuth = AppData.firebaseAuth;

        firstNameText = findViewById(R.id.first_name_text);
        lastNameText = findViewById(R.id.last_name_text);
        emailText = findViewById(R.id.new_email_text);
        passwordText = findViewById(R.id.new_password_text);
        signUpBtn = findViewById(R.id.sign_up_btn);
        backBtn = findViewById(R.id.back_btn);

        updateButtonStatus(false);
        addTextListeners();
        setupFloatingLabelErrors();

    }

    private void setupFloatingLabelErrors() {
        final TextInputLayout floatingEmailLabel = findViewById(R.id.email_text_input_layout);
        floatingEmailLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                //@oxy.edu 8
                int length = text.length();
                String email = text.toString();
                if(length > 8) {
                    if(!email.substring(length - 8).equals(OXY_EMAIL)) {
                        floatingEmailLabel.setError(getString(R.string.email_alert));
                        floatingEmailLabel.setErrorEnabled(true);
                    } else {
                        floatingEmailLabel.setErrorEnabled(false);
                    }
                } else {
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
        final TextInputLayout floatingPasswordLabel = findViewById(R.id.password_text_input_layout);
        floatingPasswordLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                int length = text.length();
                String password = text.toString();
                if(length > 0 && length < 6) {
                    floatingPasswordLabel.setError(getString(R.string.password_length_alert));
                    floatingPasswordLabel.setErrorEnabled(true);
                } else {
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
    }

    public void onSignUpBack(View view) {
        Intent intent =  new Intent(mContext, LoginActivity.class);
        startActivity(intent);
    }
    public void onSignUp(View view) {
        // only let users click sign up when button status is enabled
        if(signUpBtn.isEnabled()) {
            signUp();
        } else {
            Log.d(TAG, "can't click on sign up, complete all fields!");
        }
    }
    private void signUp() {
        final String firstName = firstNameText.getText().toString();
        final String lastName = lastNameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        final Intent newsFeedIntent = new Intent(mContext, MainActivity.class);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                    // TODO: if email already exists in database (FirebaseAuthUserCollisionException)
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(mContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void writeNewUser(FirebaseUser user, String first, String last) {
        // make a UserProfile, needed for Firebase Authentication
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

        // make a User object.
        // add User obj to FirebaseDatabase (this is separate from Firebase Authentication)
        // allows us to store other objs that are related to specific User (items, settings, etc)
        User newUser = new User(first, last, user.getEmail(), user.getUid());
        Map<String, Object> userValues = newUser.toMap();
        AppData.userRootReference.child(user.getUid()).setValue(userValues);
    }

    private void addTextListeners() {
        firstNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isFirstFilled = s.length() != 0;

                updateButtonStatus(isFirstFilled && isLastFilled &&
                        isEmailFilled && isPasswordFilled);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isFirstFilled = s.length() != 0;

                updateButtonStatus(isFirstFilled && isLastFilled &&
                        isEmailFilled && isPasswordFilled);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // maybe remove last name? not needed?
        lastNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isLastFilled = s.length() != 0;

                updateButtonStatus(isFirstFilled && isLastFilled &&
                        isEmailFilled && isPasswordFilled);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isLastFilled = s.length() != 0;

                updateButtonStatus(isFirstFilled && isLastFilled &&
                        isEmailFilled && isPasswordFilled);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // TODO: make sure email is a valid oxy.edu
        emailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isEmailFilled = s.length() != 0;

                updateButtonStatus(isFirstFilled && isLastFilled &&isEmailFilled && isPasswordFilled);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isEmailFilled = s.length() != 0;

                updateButtonStatus(isFirstFilled && isLastFilled &&
                        isEmailFilled && isPasswordFilled);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // TODO: make users have passwords longer than 6 characters
        // (firebase does not allow passwords < 6)
        passwordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isPasswordFilled = s.length() != 0;

                updateButtonStatus(isFirstFilled && isLastFilled &&
                        isEmailFilled && isPasswordFilled);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isPasswordFilled = s.length() != 0;

                updateButtonStatus(isFirstFilled && isLastFilled &&
                        isEmailFilled && isPasswordFilled);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void updateButtonStatus(boolean filled) {
        if(filled) {
            signUpBtn.setClickable(true);
            signUpBtn.setEnabled(true);
            signUpBtn.setAlpha(1f);
        } else {
            signUpBtn.setClickable(false);
            signUpBtn.setEnabled(false);
            signUpBtn.setAlpha(.4f);
        }
        //Log.d(TAG, "sign up btn is:" + signUpBtn.isEnabled() + " and" + signUpBtn.isClickable());
    }
}
