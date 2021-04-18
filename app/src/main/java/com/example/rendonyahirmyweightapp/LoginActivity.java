package com.example.rendonyahirmyweightapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Class for the Login Activity
 */
public class LoginActivity extends AppCompatActivity {

    boolean profileExists;                      // check if user created a profile
    private EditText mUsernameEdit;             // the username edit text field
    private EditText mPasswordEdit;             // the password edit text field
    private Button mLoginButton;                // the login button
    private Button mCreateAccountButton;        // the create account button
    DBHelper dbHelper;                          // Access the database

    /**
     * Override the onCreate method
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load the appropriate theme from shared preferences
        int t = new SharedPreferencesManager(getApplicationContext()).retrieveInt("theme", SharedPreferencesManager.THEME_DEFAULT);
        if(t == 0) {
            setTheme(R.style.LightTheme);
        } else {
            setTheme(R.style.DarkTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize activity views and variables
        initializeActivityElements();

        /**
         * Create onClickListener for the login button
         * will send user to MainActivity on successful login
         */
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Run method for validating login and sending to MainActivity
                openMainActivity();
            }
        });

        // if profile already exists hide create account button
        if(profileExists) {
            mCreateAccountButton.setVisibility(View.INVISIBLE);
        } else {
            /**
             * Create onClickListener for the create account button
             * will send user to the Profile activity with details to modify
             * to display createAccount details
             */
            mCreateAccountButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Run method for sending to ProfileActivity
                    openProfileActivity();
                }
            });
        }
    }

    /**
     * Method for initializing all activity views and elements
     */
    private void initializeActivityElements() {
        // initialize the view elements
        dbHelper = new DBHelper(this);
        profileExists = dbHelper.exists();
        mUsernameEdit = findViewById(R.id.text_username);
        mPasswordEdit = findViewById(R.id.text_password);
        mLoginButton = findViewById(R.id.button_login);
        mCreateAccountButton = findViewById(R.id.button_create_accnt);
    }

    /**
     * Method for creating intent for changing to MainActivity
     */
    private void openMainActivity() {
        boolean checkLoginSuccessful = verifyLogin();
        if(checkLoginSuccessful) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.login_error),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method for creating intent for changing to Profile Activity
     */
    private void openProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        // PROFILE_ID tracks if profile activity is opening from login activity
        intent.putExtra("PROFILE_ID", 0);
        startActivity(intent);
    }

    /**
     * boolean method for verifying user login attempt
     *
     * @return      true if username && password match database
     */
    private boolean verifyLogin() {
        String usernameText = mUsernameEdit.getText().toString();
        String passwordText = mPasswordEdit.getText().toString();

        // check fields are not empty and match database fields
        if(!TextUtils.isEmpty(usernameText) && !TextUtils.isEmpty(passwordText)) {
            if(usernameText.equals(dbHelper.getUsername()) && passwordText.equals(dbHelper.getPassword())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}