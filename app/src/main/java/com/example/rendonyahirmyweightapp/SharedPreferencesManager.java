package com.example.rendonyahirmyweightapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesManager {
    public final static int THEME_DEFAULT = 0;          // Int value defining default theme
    public final static int THEME_DARK = 1;             // Int value defining dark theme
    public final static int UNIT_POUNDS = 0;
    public final static int UNIT_KILOGRAMS = 1;
    private SharedPreferences sPreferences;             // SharedPreferences to store the settings
    private SharedPreferences.Editor sEditor;           // Editor to make changes on sharedPreferences
    private Context context;                            // the class

    /**
     * Constructor for the shared preference class
     *
     * @param context
     */
    public SharedPreferencesManager(Context context){
        this.context = context;
        sPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Method for getting the editor
     *
     * @return
     */
    private SharedPreferences.Editor getEditor(){
        return sPreferences.edit();
    }

    /**
     * Method for storing boolean into shared preferences
     *
     * @param tag       identifies the value
     * @param value     the value itself
     */

    public void storeBoolean(String tag, boolean value){
        sEditor = getEditor();
        sEditor.putBoolean(tag,value);
        sEditor.commit();
    }

    /**
     * Method for storing int into shared preferences
     *
     * @param tag           identifies the value
     * @param defValue      the value itself
     */
    public void storeInt(String tag, int defValue){
        sEditor = getEditor();
        sEditor.putInt(tag, defValue);
        sEditor.commit();
    }

    /**
     * Method for getting boolean from shared preferences
     *
     * @param tag           identifies the value
     * @param defValue      default value
     * @return              the stored or default value
     */

    public boolean retrieveBoolean(String tag, boolean defValue){
        return sPreferences.getBoolean(tag,defValue);

    }

    /**
     *Method for getting int from shared preferences
     *
     * @param tag           identifies the value
     * @param defValue      default value
     * @return              the stored or default value
     */
    public int retrieveInt(String tag, int defValue){
        return sPreferences.getInt(tag, defValue);
    }
}
