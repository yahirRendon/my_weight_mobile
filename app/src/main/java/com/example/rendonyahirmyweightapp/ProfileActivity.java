package com.example.rendonyahirmyweightapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * class for the Profile Activity
 *
 * This class doubles as the create account activity for new users
 */
public class ProfileActivity extends AppCompatActivity {
    private int profileID = 0;              // Determines the type of information to populate in the class
                                            // Profile vs Create class 0 by default is create account  1 is profile

    DBHelper dbHelper;                      // database access
    private Button mUpdateProfileButton;    // button that will access profile
    private ImageButton mProfileButton;     // button for going to profile activity
    private ImageButton mSettingsButton;    // button for going to settings activity
    private TextView mInstructionsText;     // TextView the instructors user about activity
    private EditText mFirstNameEdit;        // editText for entering first name
    private EditText mUsernameEdit;         // editText for entering username
    private EditText mPasswordEdit;         // editText for entering password
    private EditText mEmailEdit;            // editText for entering email
    private EditText mPhoneEdit;            // editText for phone number
    private EditText mTargetWeightEdit;     // editText for entering desired target weight

    /**
     * Override the onCreate method of activity
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
        setContentView(R.layout.activity_profile);

        // initialize activity views and variables
        initializeActivityElements();

        // Check where intent to open activity is coming from
        // update to show create account or profile
        // profileID 0 : Create Account | profileID 1: Update Profile
        Intent mIntent;
        mIntent = getIntent();
        profileID = getIntent().getIntExtra("PROFILE_ID", 0);

        // create profile account
        if(profileID == 0) {
            // disable settings and profile buttons when creating a profile for first time
            mSettingsButton.setVisibility(View.INVISIBLE);
            mProfileButton.setVisibility(View.INVISIBLE);
            mInstructionsText.setText(R.string.profile_createAcct_instruction);
            mUpdateProfileButton.setText("JOIN");

            /**
             * set on click listener for updating profile button
             */
            mUpdateProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createProfileDatabase();
                }
            });

        // update profile
        } else {
            // set instructions for updating profile and update button
            mInstructionsText.setText(R.string.profile_updateProfile_instruction);
            mUpdateProfileButton.setText("UPDATE");

            // set hint text within edit text fields with corresponding data from data tables
            updateEditFields();

            /**
             * set on click listener for updating profile button
             */
            mUpdateProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateProfileDatabase();
                }
            });

            mSettingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openSettingsActivity();
                }
            });
        }
    }

    /**
     * Method for initializing all activity elements and variables
     */
    private void initializeActivityElements() {
        // Initialize activity view elements
        dbHelper = new DBHelper(this);
        mInstructionsText = findViewById(R.id.text_instruction_createUpdate);
        mFirstNameEdit = findViewById(R.id.edit_change_name);
        mUsernameEdit = findViewById(R.id.edit_change_username);
        mPasswordEdit = findViewById(R.id.edit_change_password);
        mEmailEdit = findViewById(R.id.edit_change_email);
        mPhoneEdit = findViewById(R.id.edit_change_phone);
        mTargetWeightEdit = findViewById(R.id.edit_change_targetWeight);
        mUpdateProfileButton = findViewById(R.id.button_update_profile);
        mSettingsButton = findViewById(R.id.button_settings);
        mProfileButton = findViewById(R.id.button_profile);
        mPasswordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    /**
     * Method for updating the hints in profile edit texts
     */
    private void updateEditFields() {
        // set the edit text fields to current data set in data table
        mFirstNameEdit.setHint(dbHelper.getName());
        mUsernameEdit.setHint(dbHelper.getUsername());
        mPasswordEdit.setHint(dbHelper.getPassword());
        mEmailEdit.setHint(dbHelper.getEmail());
        String phoneNumber = dbHelper.getPhone();
        String areaCode = phoneNumber.substring(0, 3);
        String partOne = phoneNumber.substring(3, 6);
        String partTwo =phoneNumber.substring(6, 10);
        mPhoneEdit.setHint(areaCode + " " + partOne + " " + partTwo);
        mTargetWeightEdit.setHint(dbHelper.getGoalWeight());

    }

    /**
     * Method for creating user profile and updating data table
     */
    private void createProfileDatabase() {
        // set units type to pounds as default
        new SharedPreferencesManager(getApplicationContext()).storeInt("units", SharedPreferencesManager.UNIT_POUNDS);

        // get values for edit text views
        String nameText = mFirstNameEdit.getText().toString().trim();
        String usernameText = mUsernameEdit.getText().toString().trim();
        String passwordText = mPasswordEdit.getText().toString().trim();
        String emailText = mEmailEdit.getText().toString().trim();
        String phoneText = mPhoneEdit.getText().toString().trim();
        String targetWeightText = mTargetWeightEdit.getText().toString().trim();

        // insert profile edit text values into profile data table and goal data table
        boolean checkInsertProfileData = dbHelper.insertProfileTable(nameText, usernameText, passwordText, emailText, phoneText, targetWeightText);
//        boolean checkInsertTargetData = dbHelper.insertGoalTable(targetWeightText);

        // verify profile was created or not
//        if(checkInsertProfileData && checkInsertTargetData) {
        if(checkInsertProfileData) {
            // reset login activity to prevent multiple profile entries
            resetLogin();
            openMainActivity();
            // go to main activity on profile creation
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.profile_created_toast),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.profile_errorCreating_toast),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method for checking and attempting and update profile database
     */
    private void updateProfileDatabase() {
        int numChanges = 0;             // track number of fields changed in profile
        int errorChanges = 0;           // track number of errors attempting to update fields

        // get text from all profile edit text fields and set to strings
        String nameText = mFirstNameEdit.getText().toString().trim();
        String usernameText = mUsernameEdit.getText().toString().trim();
        String passwordText = mPasswordEdit.getText().toString().trim();
        String emailText = mEmailEdit.getText().toString().trim();
        String phoneText = mPhoneEdit.getText().toString().trim();
        String goalWeightText = mTargetWeightEdit.getText().toString();

        // check that name field is not empty and update name in data table
        // increment appropriate change var on successful or not
        if(!TextUtils.isEmpty(nameText)) {
            boolean checkInsert = dbHelper.updateName(nameText);
            if (checkInsert) {
                numChanges++;
            }  else {
                errorChanges ++;
            }
        }

        // check that username field is not empty and update username in data table
        // increment appropriate change var on successful or not
        if(!TextUtils.isEmpty(usernameText)) {
            boolean checkInsert = dbHelper.updateUsername(usernameText);
            if (checkInsert) {
                numChanges++;
            }  else {
                errorChanges ++;
            }
        }

        // check if password field is not empty and update password in data table
        // increment appropriate change var on successful or not
        if(!TextUtils.isEmpty(passwordText)) {
            boolean checkInsert = dbHelper.updatePassword(passwordText);
            if (checkInsert) {
                numChanges++;
            }  else {
                errorChanges ++;
            }
        }

        // check if email field is not empty and update email in data table
        // increment appropriate change var on successful or not
        if(!TextUtils.isEmpty(emailText)) {
            boolean checkInsert = dbHelper.updateEmail(emailText);
            if (checkInsert) {
                numChanges++;
            }  else {
                errorChanges ++;
            }
        }

        // check if phone field is not empty and update phone in data table
        // also do phone string length check to ensure valid 10 digit phone
        // increment appropriate change var on successful or not
        if(!TextUtils.isEmpty(phoneText) && TextUtils.isDigitsOnly(phoneText)) {
            if(phoneText.length() < 10 || phoneText.length() > 10) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.profile_phoneInvalid_toast),
                        Toast.LENGTH_SHORT).show();
            } else {
                boolean checkInsert = dbHelper.updatePhone(phoneText);
                if (checkInsert) {
                    numChanges++;
                } else {
                    errorChanges++;
                }
            }
        }

        // check if goal weight field is not empty and update goal weight in data table
        // increment appropriate change var on successful or not
        if(!TextUtils.isEmpty(goalWeightText) && TextUtils.isDigitsOnly(goalWeightText)) {
            boolean checkInsertGoalWeight = dbHelper.updateGoalWeight(goalWeightText);
            if (checkInsertGoalWeight) {
                numChanges++;
                // new goal set so set to false
                MainActivity.setNewGoal = false;
            } else {
                errorChanges++;
            }
        }

        // if no changes to edit fields were made inform user no updates made
        if(numChanges == 0 && errorChanges == 0) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.profile_noUpdate_toast),
                    Toast.LENGTH_SHORT).show();
            // if at least one edit field is modified and no errors inform user of profile update
        } else if (numChanges > 0 && errorChanges == 0) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.profile_updated_toast),
                    Toast.LENGTH_SHORT).show();
            // method for updating profile
            updateEditFields();
            // if errors inform user of error updating profile
        } else if(errorChanges > 0) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.profile_errorUpdate_toast),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method for creating intent for changing to MainActivity
     */
    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Method for creating inetent for changing to SettingsActivity
     */
    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * method for resting the login activity to prevent adding multiple user profiles
     */
    public void resetLogin() {
        // create intent to restart login activity
        Intent intentLogin = new Intent(getApplicationContext(), LoginActivity.class);
        // start activity
        startActivity(intentLogin);
        finish();
    }
}