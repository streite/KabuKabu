package com.mairyu.app.kabukabu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

//==================================================================================================
//===   StockInfo
//==================================================================================================
public class StockInfo extends AppCompatActivity {

    private Toolbar mToolbar;

    private PreferenceSettings _appPrefs;

    SQLhandler sqlHandler;
    int SQL_Stock_ID;

    EditText edtStockInfoTicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_info);

        Bundle extras = getIntent().getExtras();
        SQL_Stock_ID = extras.getInt("SQL_STOCK_ID");

        //------------------------------------------------------------------------------------------
        //---   Preference/Settings
        //------------------------------------------------------------------------------------------

        _appPrefs = new PreferenceSettings(getApplicationContext());

        //------------------------------------------------------------------------------------------
        //---   Toolbar
        //------------------------------------------------------------------------------------------

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //------------------------------------------------------------------------------------------
        //---   SQLite Setup
        //------------------------------------------------------------------------------------------

        sqlHandler = new SQLhandler(StockInfo.this, _appPrefs.getSQLDBName(), Integer.parseInt(_appPrefs.getSQLDBVersion()));

        //------------------------------------------------------------------------------------------
        //---   Layout
        //------------------------------------------------------------------------------------------

        edtStockInfoTicker = (EditText) findViewById(R.id.edtStockInfoTicker);

        //------------------------------------------------------------------------------------------
        //---   Wait until activity is stable
        //------------------------------------------------------------------------------------------
        findViewById(R.id.rlv_stock_info).post(new Runnable() {

            public void run() {

                Stock tmpStock = sqlHandler.getStockByID(SQL_Stock_ID);

                edtStockInfoTicker.setText(tmpStock.getTicker());
            }
        });

    }
}
