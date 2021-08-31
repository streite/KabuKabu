package com.mairyu.app.kabukabu;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

public class SharedPreferences extends AppCompatActivity {

    static final String SQL_FILE_NAME = "KabuKabu.db";
    static final String SQL_VERSION = "7";

//    private static final String APP_SHARED_PREFS = SharedPreferences.class.getSimpleName(); //  Name of the file -.xml
    static final String APP_SHARED_PREFS = "com.mairyu.app.kabukabu.PREFERENCE_FILE_KEY"; //  Name of the file -.xml

    android.content.SharedPreferences _sharedPrefs;
    android.content.SharedPreferences.Editor _prefsEditor;

    //------------------------------------------------------------------------------------------
    //---   Constructor
    //------------------------------------------------------------------------------------------
    public SharedPreferences(Context context) {

        _sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);

        _prefsEditor = _sharedPrefs.edit();
    }

    //----------------------------------------------------------------------------------------------
    //---   Stock SQL DB Name
    //----------------------------------------------------------------------------------------------

    public String getSQLStockDBName() {

        return _sharedPrefs.getString(SQL_FILE_NAME, SQL_FILE_NAME);
    }

    public void setSQLStockDBName(String text) {

        _prefsEditor.putString(SQL_FILE_NAME, text);

        _prefsEditor.apply();
    }

    //----------------------------------------------------------------------------------------------
    //---   Stock SQL DB Version
    //----------------------------------------------------------------------------------------------

    public String getSQLStockDBVersion() {

        return _sharedPrefs.getString(SQL_VERSION, SQL_VERSION);
    }

    public void setSQLStockDBVersion(String text) {

        _prefsEditor.putString(SQL_VERSION, text);

        _prefsEditor.apply();
    }
}