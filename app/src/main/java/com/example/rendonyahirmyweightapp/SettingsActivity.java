package com.example.rendonyahirmyweightapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import java.util.jar.Manifest;

/**
 * Class for settings activity
 */
public class SettingsActivity extends AppCompatActivity {

    private Switch mDarkModeSwitch;                 // switch for toggling dark mode
    private RadioGroup mUnitsRadioGroup;            // radio group for unit display
    private RadioButton mRadioPoundsButton;         // radio button for pound option
    private RadioButton mRadioKiloButton;           // radio button for kg option
    private ImageButton mProfileButton;             // profile button
    DBHelper dbHelper;                              // access database methods

    /**
     * On create method for activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load the appropriate theme from shared preferences
        int t = new SharedPreferencesManager(SettingsActivity.this).retrieveInt("theme", SharedPreferencesManager.THEME_DEFAULT);
        if(t == 0) {
            setTheme(R.style.LightTheme);
        } else {
            setTheme(R.style.DarkTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // initialize activity
        initializeActivityElements();

        // update the shared preferences and theme toggle switch
        updatePref();

        /**
         * Set on click listener for profile button to launch profile activity
         */
        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfileActivity();
            }
        });

        /**
         * Set on change listener for the theme switch
         */
        mDarkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // check if switch is checked and update theme in shared preferences and reset activity
                if(isChecked) {
                    new SharedPreferencesManager(getApplicationContext()).storeInt("theme", SharedPreferencesManager.THEME_DARK);
                    new SharedPreferencesManager(getApplicationContext()).storeBoolean("theme_changed", true);
                    reset();

                } else {
                    new SharedPreferencesManager(getApplicationContext()).storeInt("theme", SharedPreferencesManager.THEME_DEFAULT);
                    new SharedPreferencesManager(getApplicationContext()).storeBoolean("theme_changed", true);
                    reset();
                }
            }
        });

        /**
         * Set change listener for radio group unit
         */
        mUnitsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i) {
                    // update preferences to show pounds as units
                    case R.id.radioButton_pounds:
                        new SharedPreferencesManager(getApplicationContext()).storeInt("units", SharedPreferencesManager.UNIT_POUNDS);
                        break;
                        // Update preferences to show kg as units
                    case R.id.radioButton_kilograms:
                        new SharedPreferencesManager(getApplicationContext()).storeInt("units", SharedPreferencesManager.UNIT_KILOGRAMS);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * Method that initializes all activity elements and variables
     */
    private void initializeActivityElements() {
        // initialize activity elements
        dbHelper = new DBHelper(this);
        mDarkModeSwitch = findViewById(R.id.switch_darkMode);
        mUnitsRadioGroup = findViewById(R.id.radioGroup_units);
        mRadioPoundsButton = findViewById(R.id.radioButton_pounds);
        mRadioKiloButton = findViewById(R.id.radioButton_kilograms);
        mProfileButton = findViewById(R.id.button_profile);

        // get current unit type preference from preferences
        int unitInt = new SharedPreferencesManager(getApplicationContext()).retrieveInt("units", SharedPreferencesManager.UNIT_POUNDS);
        if(unitInt == 0) {
            mUnitsRadioGroup.check(R.id.radioButton_pounds);
        } else {
            mUnitsRadioGroup.check(R.id.radioButton_kilograms);
        }
    }

    /**
     * Method for changing to ProfileActivity
     * will create intent with value informing ProfileActivity to display Profile
     */
    private void openProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        // PROFILE_ID tracks if profile activity is opening from main activity
        intent.putExtra("PROFILE_ID", 1);
        startActivity(intent);
    }

    /**
     * Method for updating the shared preferences and setting the darkMode
     * toggle switch
     */
    public void updatePref() {
        // get the theme value
        int t = new SharedPreferencesManager(SettingsActivity.this).retrieveInt("theme", SharedPreferencesManager.THEME_DEFAULT);

        // set dark mode switch accordingly
        if(t == 0) {
            mDarkModeSwitch.setChecked(false);
        } else {
            mDarkModeSwitch.setChecked(true);
            // set radio button text to white on dark theme
            mRadioPoundsButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            mRadioKiloButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        }
    }

    /**
     * method for resting the current and setting activity to apply theme changes.
     */
    public void reset() {
        // create intent to restart main activity and settings activity
        Intent intentMain = new Intent(getApplicationContext(), MainActivity.class);
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        // start activities
        startActivity(intentMain);
        startActivity(intent);
        finish();
    }
}