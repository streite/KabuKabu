package com.mairyu.app.kabukabu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class SettingsPage extends AppCompatActivity {

    private Toolbar mToolbar;

    private PreferenceSettings _appPrefs;

    //**********************************************************************************************
    //***   onCreate
    //**********************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_page);

        //------------------------------------------------------------------------------------------
        //---   Preference/Settings
        //------------------------------------------------------------------------------------------

        _appPrefs = new PreferenceSettings(getApplicationContext());

        Log.i("LOG: (SP) SQL Filename", _appPrefs.getSQLDBName());

        //------------------------------------------------------------------------------------------
        //---   Toolbar
        //------------------------------------------------------------------------------------------

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //------------------------------------------------------------------------------------------
        //---   Layout
        //------------------------------------------------------------------------------------------

    }

    //**********************************************************************************************
    //***   onCreateOptionsMenu
    //**********************************************************************************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    //**********************************************************************************************
    //***   onOptionsItemSelected
    //**********************************************************************************************
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

//        if (id == R.id.edit) {
//
//            Log.i("LOG: (SP2) SQL Filename", "SQL " + SQL_Filename);
//
//            _appPrefs.setSQLDBName(SQL_Filename);
//
//            Log.i("LOG: (SP3) Language", "LANG " + _appPrefs.getLanguage());
//            Log.i("LOG: (SP3) SQL Filename", "SQL " + _appPrefs.getSQLDBName());
//            Log.i("LOG: (SP) ECount", _appPrefs.getEnglishCount() + "");
//
//            // Not sure why I need this ... it shows up as CANCELLED otherwise
//            setResult(Activity.RESULT_OK);
//
//            finish();
//        }

        return super.onOptionsItemSelected(item);
    }
}
