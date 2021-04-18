package com.example.rendonyahirmyweightapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * Class for the MainActivity
 */
public class MainActivity extends AppCompatActivity implements EntryAdapter.OnNoteListener {

    private int STORAGE_PERMISSION_CODE = 1;                // for tracking SMS permission code

    // Declare RecyclerView, manage items with adapter, and inflatable layout
    private RecyclerView mRecyclerView;
    private EntryAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private FloatingActionButton mAddDailyWeightFAB;        // the FAB button for adding new weight data entries
    private ImageButton mProfileButton;                     // the profile button for access user profile
    private ImageButton mSettingsButton;                    // the settings button for access of user settings
    private ProgressBar mTargetProgressBar;                 // track user progress towards target weight goal
    private TextView mCurrentWeightText;                    // the text view for current weight in dashboard
    private TextView mWeightDifferenceText;                 // the text view for dif. in current and target weight in dashboard
    private TextView mTargetWeightText;                     // the text view for target weight in dashboard
    private TextView mDataTitleText;                        // the text view for the title of the data entries
    private ArrayList<EntryItem> mExampleList;              // list holding objects that display the daily weight entry
    public static boolean setNewGoal = false;               // track if target/goal weight has been met
    DBHelper dbHelper;                                      // access the database
    boolean SMSEnabled;

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
        setContentView(R.layout.activity_main);

        // initialize all activity views and variables
        initializeActivityElements();

        // update the dashboard elements
        updateDashboard();

        /**
         * Create onClick Listener for profile button
         * will send user to the ProfileActivity
         */
        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Run the method for changing activity
                openProfileActivity();
            }
        });

        /**
         * Create onClick listener for the settings button allowing user to go to
         * the settings activity and change settings
         */
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettingsActivity();
            }
        });


        /**
         * Create onClick listener for FAB button which will allow user to add new weight data
         * will open an custom alert dialogue for entering weight data
         */
        mAddDailyWeightFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Build dialog alert and inflate
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.activity_dialog_add, null);
                // Declare and initialize the alert elements
                EditText mNewWeight = mView.findViewById(R.id.edit_new_weight);
                Button mUpdateWeightDataAlertButton = mView.findViewById(R.id.button_add_data);

                // Display the alert dialogue
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();

                /**
                 * Create onClickListener for the button within AlertDialogue
                 * this will update data table for updating database and RecyclerView
                 */
                mUpdateWeightDataAlertButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Get the edit text for new weight and set it to a string
                        String tempWeight = mNewWeight.getText().toString();

                        // Check that the new weight is not empty and update data table
                        // Also update the RecyclerView to display new entry. done in insertDataItem
                        // Prompt user to enter weight if empty
                        if (!tempWeight.isEmpty()) {
                            dialog.dismiss(); // close alert dialog
//                            mAddDailyWeightFAB.setEnabled(false); //

                            // method that will update data table and Recycler view
                            insertDataItem(tempWeight);
                            buildRecyclerView();
                            updateDashboard();

                            // display toast to inform user
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.main_weight_added_toast),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // if no weight was entered inform user to enter weight
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.main_invalid_weight_toast),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    /**
     * override on resume method
     */
    @Override
    public void onResume() {
        // on resume recreate the theme based on the theme
        boolean t = new SharedPreferencesManager(getApplicationContext()).retrieveBoolean("theme_changed", false);
        if(t) {
            recreate();
            new SharedPreferencesManager(getApplicationContext()).storeBoolean("theme_changed", false);
        }

        // update the dashboard element and rebuild recyclerview
        updateDashboard();
        buildRecyclerView();
        super.onResume();
    }

    /**
     * override the permission request
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SMSEnabled = true;
                // Toast.makeText(getApplicationContext(), "SMS Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                SMSEnabled = false;
                // Toast.makeText(getApplicationContext(), "SMS Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Method for initializing activity elements and variables
     */
    private void initializeActivityElements() {
        dbHelper = new DBHelper(this);

        // Request permission to use SMS messages
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, STORAGE_PERMISSION_CODE);

        // crate data for ExampleList the populates recycler view
        createExampleList();

        // Build RecyclerView, Adapter, LayoutManger
        buildRecyclerView();

        // initialize elements to associated ids
        mTargetProgressBar = findViewById(R.id.progBar_target);
        mProfileButton = findViewById(R.id.button_profile);
        mSettingsButton = findViewById(R.id.button_settings);
        mAddDailyWeightFAB = findViewById(R.id.button_add_weight);
        mCurrentWeightText = findViewById(R.id.text_current_weight);
        mWeightDifferenceText = findViewById(R.id.text_diff_target);
        mTargetWeightText = findViewById(R.id.text_target_weight);
        mDataTitleText = findViewById(R.id.text_dataTitle);
    }

    /**
     * Method for changing to SettingsActivity
     * will create intent
     */
    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
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
     * Method for updating the dashboard display showing the current weight
     * the target weight, the difference in the two, and the progress bar
     */
    private void updateDashboard() {
        // get current and target values and set to integers
        // create variable for calculating the difference to target weight
        String tWeightText = dbHelper.getGoalWeight();
        int tTargetWeightInt = Integer.parseInt(tWeightText);
        int currentWeightInt = tTargetWeightInt;
        int differenceInt = 0;

        // set the dashboard appropriately if weight data entries exist or not
        if (mExampleList.size() > 0) {

            // calculate difference between current weight and target weight
            currentWeightInt = Integer.parseInt(mExampleList.get(0).getWeight());
            differenceInt = currentWeightInt - tTargetWeightInt;

            // calculate the percentage from current to target weight goal and update progress bar
            float percentToGoal = 100 - (((float)Math.abs(differenceInt) / (float)tTargetWeightInt) * 100);
            mTargetProgressBar.setProgress(Math.round(percentToGoal));

            // Update the dashboard text views with appropriate weight data
            mCurrentWeightText.setText(mExampleList.get(0).getWeight());
            mTargetWeightText.setText(dbHelper.getGoalWeight());
            mWeightDifferenceText.setText(String.valueOf(differenceInt));
            mDataTitleText.setText("Weight Entries");


            // Check if the target weight goals has been met
            if(currentWeightInt == tTargetWeightInt && !setNewGoal) {

                if(SMSEnabled) {
                    // get user's phone from data table and prep congratulatory message
                    String phoneNumber = dbHelper.getPhone();
                    String SMS = getString(R.string.main_goal_achieved_sms);

                    // Create and send SMS message to user
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(phoneNumber, null, SMS, null, null);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.main_goal_achieved_toast),
                            Toast.LENGTH_LONG).show();
                }

                // user has to set a new goal since it has been achieved
                // prevents sending multiple messages
                setNewGoal = true;
            }

        } else {
            // If no weight data present sent current and difference text to empty and update target weight
            mCurrentWeightText.setText("");
            mWeightDifferenceText.setText("");
            mTargetWeightText.setText(dbHelper.getGoalWeight());
        }
    }

    /**
     * Method for inserting data into the data table and RecyclerView
     *
     * @param tWeightData       the weight value
     */
    public void insertDataItem(String tWeightData) {
        // get the weight value and pass to method in data base that inserts data in
        // the weight table
        boolean checkInsert =  dbHelper.insertWeightTable(tWeightData);
        if(checkInsert) {
            // update the array list with all the data in the weight table
            mExampleList = dbHelper.getAllData();
            // move to the top of the recycler view layout
            mLayoutManager.scrollToPosition(0);
            // inform adapter that new data has been updated
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Method for removing data from the data table and the RecyclerView
     *
     * @param position the index position for item to be removed
     */
    public void removeDataItem(int position) {
        // Remove entry at desired position
        mExampleList.remove(position);

        // notify adapter of change
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Method for changing data in the data table
     *
     * @param position the index position of data to be changed
     * @param weight     the new weight data
     */
    public void changeItem(int position, String weight) {
        // Update the weight at desired position within weight list
        mExampleList.get(position).changeWeight(weight);
        mAdapter.notifyItemChanged(position);
    }

    /**
     * Method for populating the dummy data
     */
    public void createExampleList() {
        // initialize array list
        mExampleList = new ArrayList<EntryItem>();

        // populate array list with all data from weight table
        mExampleList = dbHelper.getAllData();
    }

    /**
     * Method for building the RecyclerView based on the data table
     */
    public void buildRecyclerView() {
        // find the recycler view layout out build layout
        mRecyclerView = findViewById(R.id.recyclerView_weight_data);
        mLayoutManager = new LinearLayoutManager(this);

        int tempUnitValue = new SharedPreferencesManager(getApplicationContext()).retrieveInt("units", SharedPreferencesManager.UNIT_POUNDS);

        // update the array list with all data in the weight table
        mExampleList = dbHelper.getAllData();
        mAdapter = new EntryAdapter(mExampleList, this, tempUnitValue);

        // display the recycler view with data
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Override the custom Click method to the Adapter class
     *
     * @param position the index position of item clicked
     */
    @Override
    public void onNoteClickDelete(int position) {
        // set variable to the id from the weight table of the selected item
        int pos = mExampleList.get(position).getId();

        // delete entry at desired location
        boolean checkDelete = dbHelper.deleteWeightEntry(pos);
        if (checkDelete) {
            // rebuild recycler view
            buildRecyclerView();

            // update the dashboard display
            updateDashboard();

            // display toast to inform user an entry was deleted
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.main_entryDelete_toast),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Override the custom click method to the adapter class
     *
     * @param position the index position of the item clicked
     */
    @Override
    public void onNoteClickEdit(int position) {
        // set variable to the id from the weight table of the selected item
        int pos = mExampleList.get(position).getId();

        // Build dialog alert and inflate
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.activity_dialog_add, null);

        // Declare and initialize the alert elements
        EditText mNewWeight = mView.findViewById(R.id.edit_new_weight);
        Button mUpdateWeightDataAlertButton = mView.findViewById(R.id.button_add_data);

        // set the text for the alert items
        mNewWeight.setHint(R.string.main_alert_UpdateWeight);
        mUpdateWeightDataAlertButton.setText(R.string.main_alert_updateButton);

        // Display the alert dialogue
        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();

        /**
         * Create onClickListener for the button within AlertDialogue
         * this will update data table for updating database and RecyclerView
         */
        mUpdateWeightDataAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the edit text for new weight and set it to a string
                String tempWeight = mNewWeight.getText().toString();

                // Check that the new weight is not empty and update data table
                // Also update the RecyclerView to display new entry. done in insertDataItem
                // Prompt user to enter weight if empty
                if (!tempWeight.isEmpty()) {
                    dialog.dismiss(); // close alert dialog

                    // method that will update data table and Recycler view
                    boolean checkEntry = dbHelper.updateWeightEntry(pos, tempWeight);
                    if (checkEntry) {
                        buildRecyclerView();
                        updateDashboard();
                        // display toast to inform user
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.main_entryModified_toast),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // if no weight was entered inform user to enter weight
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.main_invalidWeight_toast),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}