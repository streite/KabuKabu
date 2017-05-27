package com.mairyu.app.kabukabu;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

public class PreferenceSettings extends AppCompatActivity {

    public static final String SQL_FILE_NAME = "KabuKabu.db";
    public static final String SQL_VERSION = "5";

    private static final String APP_SHARED_PREFS = PreferenceSettings.class.getSimpleName(); //  Name of the file -.xml

    private SharedPreferences _sharedPrefs;
    private SharedPreferences.Editor _prefsEditor;

    //------------------------------------------------------------------------------------------
    //---   Constructor
    //------------------------------------------------------------------------------------------
    public PreferenceSettings(Context context) {

        _sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);

        _prefsEditor = _sharedPrefs.edit();
    }

    //----------------------------------------------------------------------------------------------
    //---   SQL DB Name
    //----------------------------------------------------------------------------------------------

    public String getSQLDBName() {

        return _sharedPrefs.getString(SQL_FILE_NAME, "N_A");
    }

    public void setSQLDBName(String text) {

        _prefsEditor.putString(SQL_FILE_NAME, text);

        _prefsEditor.apply();
    }

    //----------------------------------------------------------------------------------------------
    //---   SQL DB Version
    //----------------------------------------------------------------------------------------------

    public String getSQLDBVersion() {

        return _sharedPrefs.getString(SQL_VERSION, "5");
    }

    public void setSQLDBVersion(String text) {

        _prefsEditor.putString(SQL_VERSION, text);

        _prefsEditor.apply();
    }
}