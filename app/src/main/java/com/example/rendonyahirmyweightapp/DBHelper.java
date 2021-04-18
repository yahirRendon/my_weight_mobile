package com.example.rendonyahirmyweightapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;

/**
 *  Class for creating SQLite database
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "user_database";            // database name
    private static final String TABLE_PROFILE = "user_profile";             // user profile table name
    private static final String TABLE_WEIGHT = "user_weight";               // user weight entry table name
    private static final String ROW_NAME = "name";                          // user name row name
    private static final String ROW_USERNAME = "username";                  // user username row name
    private static final String ROW_PASSWORD = "password";                  // user password row name
    private static final String ROW_EMAIL = "email";                        // user email row name
    private static final String ROW_PHONE = "phone";                        // user phone row name
    private static final String ROW_DATE = "date";                          // date entry row name
    private static final String ROW_WEIGHT = "weight";                      // weight entry row name
    private static final String ROW_GOAL_WEIGHT = "goalWeight";             // goal/target weight row name

    // Create database called user_database
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    /**
     *  The onCreate database method
     *
     * @param db        the database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // string that will create table for holding user profile data
        String table1 = "CREATE TABLE "+TABLE_PROFILE+"(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                        ROW_NAME + " TEXT, " +
                                                        ROW_USERNAME + " TEXT, " +
                                                        ROW_PASSWORD + " TEXT, " +
                                                        ROW_EMAIL + " TEXT, " +
                                                        ROW_PHONE + " TEXT, " +
                                                        ROW_GOAL_WEIGHT + " TEXT)";

        // string that will create table for hold user weight entry data
        String table2 = "CREATE TABLE "+TABLE_WEIGHT+"(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                    ROW_DATE + " TEXT, " +
                                                    ROW_WEIGHT + " TEXT)";

        // Create data tables
        db.execSQL(table1);
        db.execSQL(table2);
    }

    /**
     *  The onUpgrade database method
     *
     * @param db        the database
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        // delete the table if exists
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_WEIGHT);
        onCreate(db);
    }

    /**
     * Boolean for inserting/populating the user profile table
     * @param name                  the user's name
     * @param username              the user's username
     * @param password              the user's password
     * @param email                 the user's email
     * @return                      returns true on successful insert
     */
    public boolean insertProfileTable(String name, String username, String password, String email, String phone, String goalWeight) {
        // open database as writable
        SQLiteDatabase db = this.getWritableDatabase();

        // update with table row values
        ContentValues contentValues = new ContentValues();
        contentValues.put(ROW_NAME, name);
        contentValues.put(ROW_USERNAME, username);
        contentValues.put(ROW_PASSWORD, password);
        contentValues.put(ROW_EMAIL, email);
        contentValues.put(ROW_PHONE, phone);
        contentValues.put(ROW_GOAL_WEIGHT, goalWeight);

        // check that insert was successful
        long result = db.insert(TABLE_PROFILE, null, contentValues);
        if(result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Boolean for inserting data into the daily weight entry table
     *
     * @param weight            the weight value
     * @return                  true on successful update
     */
    public boolean insertWeightTable(String weight) {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        calendar.getTimeInMillis();

        // format current date string
        String currentDate = String.valueOf(calendar.get(Calendar.MONTH) + 1) + "/" +
                String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "/" +
                String.valueOf(calendar.get(Calendar.YEAR));

        // open database as writable
        SQLiteDatabase db = this.getWritableDatabase();

        // update values in the table rows
        ContentValues contentValues = new ContentValues();
        contentValues.put(ROW_DATE, currentDate);
        contentValues.put(ROW_WEIGHT, weight);
        long result = db.insert(TABLE_WEIGHT, null, contentValues);
        if(result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Boolean for checking if table profile exists
     * @return              true if table exists
     */
    public boolean exists() {
        // open database as readable
        SQLiteDatabase db = this.getReadableDatabase();

        // get all entries and see if table count is greater than 0
        Cursor cursor = db.rawQuery("SELECT * from "+TABLE_PROFILE, null);
        if(cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Boolean for updating the user goal/target weight in the goal data table
     *
     * @param weight            the goal/target weight
     * @return                  true on successful update
     */
    public boolean updateGoalWeight(String weight) {
        // open data table as writable
        SQLiteDatabase db = this.getWritableDatabase();

        // set desired update values
        ContentValues contentValues = new ContentValues();
        contentValues.put(ROW_GOAL_WEIGHT, weight);

        // check for successful update
        long result = db.update(TABLE_PROFILE, contentValues, "id = ?", new String[]{"1"});
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Boolean for updating the user's name in the profile data table
     *
     * @param name              the user's name
     * @return                  true on successful update
     */
    public boolean updateName(String name) {
        // open data table as writable
        SQLiteDatabase db = this.getWritableDatabase();

        // set desired update values
        ContentValues contentValues = new ContentValues();
        contentValues.put(ROW_NAME, name);

        // check for successful update
        long result = db.update(TABLE_PROFILE, contentValues, "id = ?", new String[]{"1"});
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Boolean for updating the user's username in the profile data table
     * @param username          the user's username
     * @return                  true on successful update
     */
    public boolean updateUsername(String username) {
        // open data table as writable
        SQLiteDatabase db = this.getWritableDatabase();

        // set desired update values
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        // check for successful update
        long result = db.update(TABLE_PROFILE, contentValues, "id = ?", new String[]{"1"});
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Boolean for updating the user's password in the profile data table
     * @param password          the user's password
     * @return                  true on successful update
     */
    public boolean updatePassword(String password) {
        // open data table as writable
        SQLiteDatabase db = this.getWritableDatabase();

        // set desired update values
        ContentValues contentValues = new ContentValues();
        contentValues.put(ROW_PASSWORD, password);

        // check for successful update
        long result = db.update(TABLE_PROFILE, contentValues, "id = ?", new String[]{"1"});
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Boolean for updating the user's email in the profile data table
     *
     * @param email             the user's email
     * @return                  true on successful update
     */
    public boolean updateEmail(String email) {
        // open data table as writable
        SQLiteDatabase db = this.getWritableDatabase();

        // set desired update values
        ContentValues contentValues = new ContentValues();
        contentValues.put(ROW_EMAIL, email);

        // check for successful update
        long result = db.update(TABLE_PROFILE, contentValues, "id = ?", new String[]{"1"});
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Boolean for updating the user's phone in the profile data table
     *
     * @param phone             the user's email
     * @return                  true on successful update
     */
    public boolean updatePhone(String phone) {
        // open data table as writable
        SQLiteDatabase db = this.getWritableDatabase();

        // set desired update values
        ContentValues contentValues = new ContentValues();
        contentValues.put(ROW_PHONE, phone);

        // check for successful update
        long result = db.update(TABLE_PROFILE, contentValues, "id = ?", new String[]{"1"});
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Boolean for updating a specified prior weight entry in the weight table
     *
     * @param id            the id location/value in the table
     * @param weight        the new weight value
     * @return              return true on successful update
     */
    public boolean updateWeightEntry(int id, String weight) {
        // convert id to string
        String idString = String.valueOf(id);

        // open data table as writable
        SQLiteDatabase db = this.getWritableDatabase();

        // set desired update values
        ContentValues contentValues = new ContentValues();
        contentValues.put(ROW_WEIGHT, weight);

        // check for successful update
        long result = db.update(TABLE_WEIGHT, contentValues, "id = ?", new String[]{idString});
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * String method for getting the user's goal/target weight from goal weight data table
     *
     * @return              the target/goal weight as string
     */
    public String getGoalWeight() {
        // set temp string
        String tempGoalWeight = null;

        // open data table as readable
        SQLiteDatabase db = this.getReadableDatabase();

        // prepare strings for where and args and pass to query
        String whereClause = "ID=?";
        String[] whereArgs = new String[]{String.valueOf(1)};
        Cursor cursor = db.query(TABLE_PROFILE,null,whereClause,whereArgs,null,null,null);
        if (cursor.moveToFirst()) {
            tempGoalWeight = cursor.getString(cursor.getColumnIndex(ROW_GOAL_WEIGHT));
        }

        // close cursor and db
        cursor.close();
        db.close();

        // return string
        return tempGoalWeight;
    }

    /**
     * String method for getting the user's name from profile data table
     *
     * @return              the name as string
     */
    public String getName() {
        // set temp string
        String tempName = null;

        // open data table as readable
        SQLiteDatabase db = this.getReadableDatabase();

        // prepare strings for where and args and pass to query
        String whereClause = "ID=?";
        String[] whereArgs = new String[]{String.valueOf(1)};
        Cursor cursor = db.query(TABLE_PROFILE,null,whereClause,whereArgs,null,null,null);
        if (cursor.moveToFirst()) {
            tempName = cursor.getString(cursor.getColumnIndex(ROW_NAME));
        }
        // close cursor and db
        cursor.close();
        db.close();

        // return string
        return tempName;
    }

    /**
     * String method for getting the user's username from the profile data table
     *
     * @return              the username as string
     */
    public String getUsername() {
        // set temp string
        String tempUsername = null;

        // open data table as readable
        SQLiteDatabase db = this.getReadableDatabase();

        // prepare strings for where and args and pass to query
        String whereClause = "ID=?";
        String[] whereArgs = new String[]{String.valueOf(1)};
        Cursor cursor = db.query(TABLE_PROFILE,null,whereClause,whereArgs,null,null,null);
        if (cursor.moveToFirst()) {
            tempUsername = cursor.getString(cursor.getColumnIndex(ROW_USERNAME));
        }

        // close cursor and db
        cursor.close();
        db.close();

        // return string
        return tempUsername;
    }

    /**
     * String method for getting the user's password from the profile data table
     *
     * @return              the password as string
     */
    public String getPassword() {
        // set temp string
        String tempPassword = null;

        // open data table as readable
        SQLiteDatabase db = this.getReadableDatabase();

        // prepare strings for where and args and pass to query
        String whereClause = "ID=?";
        String[] whereArgs = new String[]{String.valueOf(1)};
        Cursor cursor = db.query(TABLE_PROFILE,null,whereClause,whereArgs,null,null,null);
        if (cursor.moveToFirst()) {
            tempPassword = cursor.getString(cursor.getColumnIndex(ROW_PASSWORD));
        }

        // close cursor and db
        cursor.close();
        db.close();

        // return string
        return tempPassword;
    }

    /**
     * String method for getting the user's email from profile data table
     *
     * @return              the email as string
     */
    public String getEmail() {
        // set temp string
        String tempEmail = null;

        // open data table as readable
        SQLiteDatabase db = this.getReadableDatabase();

        // prepare strings for where and args and pass to query
        String whereClause = "ID=?";
        String[] whereArgs = new String[]{String.valueOf(1)};
        Cursor cursor = db.query(TABLE_PROFILE,null,whereClause,whereArgs,null,null,null);
        if (cursor.moveToFirst()) {
            tempEmail = cursor.getString(cursor.getColumnIndex(ROW_EMAIL));
        }

        // close cursor and db
        cursor.close();
        db.close();

        // return string
        return tempEmail;
    }

    /**
     * String method for getting the user's phone from profile data table
     *
     * @return              the phone as string
     */
    public String getPhone() {
        // set temp string
        String temPhone = null;

        // open data table as readable
        SQLiteDatabase db = this.getReadableDatabase();

        // prepare strings for where and args and pass to query
        String whereClause = "ID=?";
        String[] whereArgs = new String[]{String.valueOf(1)};
        Cursor cursor = db.query(TABLE_PROFILE,null,whereClause,whereArgs,null,null,null);
        if (cursor.moveToFirst()) {
            temPhone = cursor.getString(cursor.getColumnIndex(ROW_PHONE));
        }

        // close cursor and db
        cursor.close();
        db.close();

        // return string
        return temPhone;
    }

    /**
     * String method for getting the user's current weight from weight data table
     *
     * @return              the current weight as string
     */
    public String getCurrentWeight() {
        // set temp string
        String tempWeight = null;

        // open data table as readable
        SQLiteDatabase db = this.getReadableDatabase();

        // prepare strings for where and args and pass to query
        String whereClause = "ID=?";
        String[] whereArgs = new String[]{String.valueOf(1)};
        Cursor cursor = db.query(TABLE_WEIGHT,null,whereClause,whereArgs,null,null,null);
        if (cursor.moveToFirst()) {
            tempWeight = cursor.getString(cursor.getColumnIndex(ROW_WEIGHT));
        }

        // close cursor and db
        cursor.close();
        db.close();

        // return string
        return tempWeight;
    }

    /**
     * ArrayList method for getting an array list with all entries from weight data table
     *
     * @return                  ArrayList of all entries in table
     */
    public ArrayList<EntryItem> getAllData() {
        // create temp array list
        ArrayList<EntryItem> returnList = new ArrayList<>();

        // create general query for all items in table weight
        String queryString = "SELECT * FROM " + TABLE_WEIGHT;

        // open data table as readable
        SQLiteDatabase db = this.getReadableDatabase();

        // create cursor and loop through table
        Cursor cursor = db.rawQuery(queryString, null);
        if(cursor.moveToFirst()) {
            do {
                // store constructor args into variables
                int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
                String userDate = cursor.getString(cursor.getColumnIndex("date"));
                String userName = cursor.getString(cursor.getColumnIndex("weight"));

                // add items to array list
                returnList.add(new EntryItem(id, userDate, userName));
            } while(cursor.moveToNext());
        }

        // close cursor and db
        cursor.close();
        db.close();

        // reverse the array list to display newest entries first/top
        ArrayList<EntryItem> revList = new ArrayList<>();
        for(int i = returnList.size() - 1; i >= 0; i--) {
            revList.add(returnList.get(i));
        }

        // return array list
        return  revList;
    }

    /**
     * Boolean method for deleting specified entries fromt eh weight table
     *
     * @param id            id of the entry in the data table
     * @return              true on successful deletion
     */
    public boolean deleteWeightEntry(int id) {
        // convert id to string
        String idString = String.valueOf(id);

        // open database as writable
        SQLiteDatabase db = this.getWritableDatabase();

        // check if deletion is successful
        long result = db.delete(TABLE_WEIGHT, "id = ?", new String[] {idString} );
        db.close();
        if (result == -1) {
            return false;
        } else {

            return true;
        }
    }

    /**
     * int method for getting the id of the last entry in the weight table
     * @return
     */
    public int getLastID()
    {
        // generate query elements and return highest id value
        String COL_NAME = "id";
        SQLiteDatabase database=this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT MAX("+COL_NAME+") FROM "+TABLE_WEIGHT, null);
        int maxid = (cursor.moveToFirst() ? cursor.getInt(0) : 0);
        return maxid;
    }
}
