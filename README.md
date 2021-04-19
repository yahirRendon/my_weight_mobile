# My Weight Mobile App

This Android mobile app helps users track their weight towards a target weight. The user is able to create an account, login, enter new weight entries, update their profile, modify weight entries, and toggle preferences. A simple dashboard displays their current weight, target weight, and weight to go towards their goal. Upon reaching their target weight the app will send a SMS message congratulating them. 

This project was used as one of the artifacts in my capstone project at SNHU to demonstrate my ability to work with databases. Relevant images and code snips are below:

#### UI Design
A preview of the app activities and layout in the light and dark mode.

![my_weight_main_light](https://user-images.githubusercontent.com/33650498/115187308-ec4e6a80-a097-11eb-876f-cec492d50a27.JPG)
![my_weight_main](https://user-images.githubusercontent.com/33650498/115187325-f53f3c00-a097-11eb-8361-561fbb987d56.JPG)
![my_weight_profile](https://user-images.githubusercontent.com/33650498/115187371-0e47ed00-a098-11eb-9e4f-0fd0137f4c2c.JPG)
![my_weight_settings](https://user-images.githubusercontent.com/33650498/115187397-1738be80-a098-11eb-8733-19793e12b142.JPG)


#### Database
A preview of the user weight table after creation and entries made:

![my_weight_database](https://user-images.githubusercontent.com/33650498/115186630-ead07280-a096-11eb-96ee-db73b598fac7.JPG)

The creation of the database using a helper class was implemented:

```Java
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

```

#### Functions

An example fo the CREATE method:

```Java
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
```


An example of a READ method:

```Java
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
```

An example of an UPDATE method:

```Java
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
```

An example of a DELTE method

```Java
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
```
