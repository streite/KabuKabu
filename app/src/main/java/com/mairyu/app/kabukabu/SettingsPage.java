package com.mairyu.app.kabukabu;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class SettingsPage extends AppCompatActivity {

    private Toolbar mToolbar;

    private PreferenceSettings _appPrefs;

    private TextView txtSQLStockName;
    private TextView edtSQLStockName;
    private TextView txtSQLStockVersion;
    private TextView edtSQLStockVersion;

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

        Log.i("LOG: (SP) SQL Filename", _appPrefs.getSQLStockDBName());

        //------------------------------------------------------------------------------------------
        //---   Toolbar
        //------------------------------------------------------------------------------------------

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //------------------------------------------------------------------------------------------
        //---   Layout
        //------------------------------------------------------------------------------------------

        txtSQLStockName = (TextView) findViewById(R.id.txtSQLStockName);
        edtSQLStockName = (TextView) findViewById(R.id.edtSQLStockName);
        txtSQLStockVersion = (TextView) findViewById(R.id.txtSQLStockVersion);
        edtSQLStockVersion = (TextView) findViewById(R.id.edtSQLStockVersion);

        //------------------------------------------------------------------------------------------
        //---   Display during init
        //------------------------------------------------------------------------------------------

        edtSQLStockName.setText(_appPrefs.getSQLStockDBName());
        edtSQLStockVersion.setText(_appPrefs.getSQLStockDBVersion());
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

        if (id == R.id.action_edit) {

            _appPrefs.setSQLStockDBName(edtSQLStockName.getText().toString());

            // Not sure why I need this ... it shows up as CANCELLED otherwise
            setResult(Activity.RESULT_OK);

            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
