package com.mairyu.app.kabukabu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnPortfolio,btnYahoo,btnWSJ,btnSetting;
    Button btnGoogleSheetsRead,btnGoogleSheetsWrite;

    String Google_Sheet = "TECH";

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    static final int REQUEST_PREFERENCE = 2000;
    static final int REQUEST_SHEETS = 2000;

//    https://tradingticket.github.io/AndroidSDK/index.html

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //------------------------------------------------------------------------------------------
        //---   Layout
        //------------------------------------------------------------------------------------------

        btnPortfolio = (Button) findViewById(R.id.btnPortfolio);
        btnPortfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //------------------------------------------------------------------------------------------
                //---   Setup Layout
                //------------------------------------------------------------------------------------------

                switch (v.getId()) {

                    //--------------------------------------------------------------------------------------
                    //---   SAVE
                    //--------------------------------------------------------------------------------------
                    case R.id.btnPortfolio:

                        Intent intentCategory = new Intent(MainActivity.this, CategoryView.class);
                        startActivity(intentCategory);

                        break;

                }
            }
        });

        btnGoogleSheetsRead = (Button) findViewById(R.id.btnGoogleSheetsRead);
        btnGoogleSheetsRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //------------------------------------------------------------------------------------------
                //---   Setup Layout
                //------------------------------------------------------------------------------------------

                switch (v.getId()) {

                    //--------------------------------------------------------------------------------------
                    //---   SAVE
                    //--------------------------------------------------------------------------------------
                    case R.id.btnGoogleSheetsRead:

                        Intent intentSheet = new Intent(MainActivity.this, GoogleSheets.class);
                        intentSheet.putExtra("SHEETS_ID", "1nISn67Vft5ncSsnNfTRmFM2okQO8ttCrBtmWhFTu4bM");
                        intentSheet.putExtra("SHEETS_TAB", Google_Sheet);
                        intentSheet.putExtra("SHEETS_RANGE", Google_Sheet);
                        intentSheet.putExtra("SHEETS_MODE", "READ");

                        startActivityForResult(intentSheet, REQUEST_SHEETS);

//                        Intent intentGoogle = new Intent(MainActivity.this, GoogleSheets.class);
//                        startActivity(intentGoogle);

                        break;

                }
            }
        });

        btnGoogleSheetsWrite = (Button) findViewById(R.id.btnGoogleSheetsWrite);
        btnGoogleSheetsWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //------------------------------------------------------------------------------------------
                //---   Setup Layout
                //------------------------------------------------------------------------------------------

                switch (v.getId()) {

                    //--------------------------------------------------------------------------------------
                    //---   SAVE
                    //--------------------------------------------------------------------------------------
                    case R.id.btnGoogleSheetsWrite:

                        Intent intentSheet = new Intent(MainActivity.this, GoogleSheets.class);
                        intentSheet.putExtra("SHEETS_ID", "1nISn67Vft5ncSsnNfTRmFM2okQO8ttCrBtmWhFTu4bM");
                        intentSheet.putExtra("SHEETS_TAB", Google_Sheet);
                        intentSheet.putExtra("SHEETS_RANGE", Google_Sheet);
                        intentSheet.putExtra("SHEETS_MODE", "WRITE");

                        startActivityForResult(intentSheet, REQUEST_SHEETS);

//                        Intent intentGoogle = new Intent(MainActivity.this, GoogleSheets.class);
//                        startActivity(intentGoogle);

                        break;

                }
            }
        });


        btnYahoo = (Button) findViewById(R.id.btnYahoo);
        btnYahoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //------------------------------------------------------------------------------------------
                //---   Setup Layout
                //------------------------------------------------------------------------------------------

                switch (v.getId()) {

                    //--------------------------------------------------------------------------------------
                    //---   SAVE
                    //--------------------------------------------------------------------------------------
                    case R.id.btnYahoo:

                        Intent intentSheet = new Intent(MainActivity.this, YahooAPI.class);
                        intentSheet.putExtra("SHEETS_ID", "1nISn67Vft5ncSsnNfTRmFM2okQO8ttCrBtmWhFTu4bM");
                        intentSheet.putExtra("SHEETS_TAB", Google_Sheet);
                        intentSheet.putExtra("SHEETS_RANGE", Google_Sheet);
                        intentSheet.putExtra("SHEETS_MODE", "READ");

                        startActivityForResult(intentSheet, REQUEST_SHEETS);

//                        Intent intentGoogle = new Intent(MainActivity.this, GoogleSheets.class);
//                        startActivity(intentGoogle);

                        break;

                }
            }
        });

        btnWSJ = (Button) findViewById(R.id.btnWSJ);
        btnWSJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //------------------------------------------------------------------------------------------
                //---   Setup Layout
                //------------------------------------------------------------------------------------------

                switch (v.getId()) {

                    //--------------------------------------------------------------------------------------
                    //---   SAVE
                    //--------------------------------------------------------------------------------------
                    case R.id.btnWSJ:

                        Intent intentSheet = new Intent(MainActivity.this, WSJ.class);
                        intentSheet.putExtra("SHEETS_ID", "1nISn67Vft5ncSsnNfTRmFM2okQO8ttCrBtmWhFTu4bM");
                        intentSheet.putExtra("SHEETS_TAB", Google_Sheet);
                        intentSheet.putExtra("SHEETS_RANGE", Google_Sheet);
                        intentSheet.putExtra("SHEETS_MODE", "READ");

                        startActivityForResult(intentSheet, REQUEST_SHEETS);

//                        Intent intentGoogle = new Intent(MainActivity.this, GoogleSheets.class);
//                        startActivity(intentGoogle);

                        break;

                }
            }
        });

        btnSetting = (Button) findViewById(R.id.btnSetting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //------------------------------------------------------------------------------------------
                //---   Setup Layout
                //------------------------------------------------------------------------------------------

                switch (v.getId()) {

                    //--------------------------------------------------------------------------------------
                    //---   SAVE
                    //--------------------------------------------------------------------------------------
                    case R.id.btnSetting:

                        Intent intentSetting = new Intent(MainActivity.this, SettingsPage.class);
                        startActivityForResult(intentSetting, REQUEST_SHEETS);

                        break;

                }
            }
        });
    }
}
