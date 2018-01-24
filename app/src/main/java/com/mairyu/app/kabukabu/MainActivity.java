package com.mairyu.app.kabukabu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import java.util.List;
import java.util.Map;

//==================================================================================================
//===   MainActivity
//==================================================================================================
public class MainActivity extends AppCompatActivity {

    Button btnPortfolio,btnEarnings,btnWorldIndices,btnWSJ,btnSetting;
    Button btnGoogleSheetsRead,btnGoogleSheetsWrite;

    ExpandableListView expListView;

    ImageView KoreaFlag;

    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> laptopCollection;

    String Google_Sheet = "PORTFOLIO";

    static final int REQUEST_SHEETS = 2000;

//    https://tradingticket.github.io/AndroidSDK/index.html

    //**********************************************************************************************
    //***   onCreate
    //**********************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //------------------------------------------------------------------------------------------
        //---   Layout
        //------------------------------------------------------------------------------------------

        KoreaFlag = (ImageView) findViewById(R.id.koreaFlag);

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

        //------------------------------------------------------------------------------------------
        //---   World Indices
        //------------------------------------------------------------------------------------------
        btnWorldIndices = (Button) findViewById(R.id.btnWorldIndices);
        btnWorldIndices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //----------------------------------------------------------------------------------
                //---   Setup Layout
                //----------------------------------------------------------------------------------

                switch (v.getId()) {

                    //------------------------------------------------------------------------------
                    //---   INDICES
                    //------------------------------------------------------------------------------
                    case R.id.btnWorldIndices:

                        Intent intentSheet = new Intent(MainActivity.this, IndexView.class);
                        startActivity(intentSheet);

                        break;

                }
            }
        });

        //------------------------------------------------------------------------------------------
        //---   Earnings
        //------------------------------------------------------------------------------------------
        btnEarnings = (Button) findViewById(R.id.btnEarnings);
        btnEarnings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //----------------------------------------------------------------------------------
                //---   Setup Layout
                //----------------------------------------------------------------------------------

                switch (v.getId()) {

                    //------------------------------------------------------------------------------
                    //---   EARNINGS
                    //------------------------------------------------------------------------------
                    case R.id.btnEarnings:

                        Intent intentSheet = new Intent(MainActivity.this, EarningsView.class);
                        startActivity(intentSheet);

                        break;

                }
            }
        });

        //------------------------------------------------------------------------------------------
        //---   Top 100
        //------------------------------------------------------------------------------------------
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

        expListView = (ExpandableListView) findViewById(R.id.StockExpandList);

//        final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
//                this, groupList, laptopCollection);
//        expListView.setAdapter(expListAdapter);

    }
}
