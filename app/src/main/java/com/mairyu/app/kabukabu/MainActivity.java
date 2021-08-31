package com.mairyu.app.kabukabu;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

    FloatingActionButton fabFirebaseStock;

    static final int REQUEST_INPUT_RECIPE = 1000;
    static final int REQUEST_FIREBASE = 2000;
    static final int REQUEST_ARCHIVE = 3000;

    static final int FIREBASE_POPUP_WIDTH = 700;
    static final int FIREBASE_POPUP_HEIGHT = 400;
    static final int FIREBASE_POPUP_GRAVITY_X = 0;
    static final int FIREBASE_POPUP_GRAVITY_Y = 300;

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

                        Intent intentCategory = new Intent(MainActivity.this, PortfolioView.class);
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

                        Intent intentSheet = new Intent(MainActivity.this, WorldIndexView.class);
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

        //------------------------------------------------------------------------------------------
        //---   Firebase R/W
        //------------------------------------------------------------------------------------------

        fabFirebaseStock = findViewById(R.id.fabFirebaseStock);

        fabFirebaseStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //----------------------------------------------------------------------------------
                //---   Firebase Access Popup
                //----------------------------------------------------------------------------------

                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);

                View popupView = layoutInflater.inflate(R.layout.firebase_pop_up, null);

                final PopupWindow popupWindowFirebase = new PopupWindow(popupView, FIREBASE_POPUP_WIDTH, FIREBASE_POPUP_HEIGHT);

                popupWindowFirebase.showAtLocation(findViewById(R.id.activity_main),
                        Gravity.CENTER, FIREBASE_POPUP_GRAVITY_X, FIREBASE_POPUP_GRAVITY_Y);

                //----------------------------------------------------------------------------------
                //---   Write
                //----------------------------------------------------------------------------------

                ImageButton btnFirebasePopupWrite = popupView.findViewById(R.id.btnFirebasePopupWrite);

                btnFirebasePopupWrite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intentFirebase = new Intent(MainActivity.this, FirebaseAccessStocks.class);
                        intentFirebase.putExtra("FIREBASE_MODE", "WRITE");

                        startActivityForResult(intentFirebase,REQUEST_FIREBASE);

                        popupWindowFirebase.dismiss();
                    }
                });

                //--------------------------------------------------------------------------------------
                //---   Read
                //--------------------------------------------------------------------------------------

                ImageView btnFirebasePopupRead = popupView.findViewById(R.id.btnFirebasePopupRead);

                btnFirebasePopupRead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intentFirebase = new Intent(MainActivity.this, FirebaseAccessStocks.class);
                        intentFirebase.putExtra("FIREBASE_MODE", "READ");

                        startActivityForResult(intentFirebase,REQUEST_FIREBASE);

                        popupWindowFirebase.dismiss();

//                        initGardenLocations();
//
//                        adapterAllMatchingGardenLocations.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
