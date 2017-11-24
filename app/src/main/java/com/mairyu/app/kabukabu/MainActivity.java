package com.mairyu.app.kabukabu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button btnPortfolio,btnEarnings,btnWSJ,btnSetting;
    Button btnGoogleSheetsRead,btnGoogleSheetsWrite;

    ExpandableListView expListView;

    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> laptopCollection;

    String Google_Sheet = "PORTFOLIO";

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

        createGroupList();

        createCollection();

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


        btnEarnings = (Button) findViewById(R.id.btnEarnings);
        btnEarnings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //------------------------------------------------------------------------------------------
                //---   Setup Layout
                //------------------------------------------------------------------------------------------

                switch (v.getId()) {

                    //--------------------------------------------------------------------------------------
                    //---   EARNINGS
                    //--------------------------------------------------------------------------------------
                    case R.id.btnEarnings:

                        Intent intentSheet = new Intent(MainActivity.this, EarningsView.class);
                        startActivity(intentSheet);

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

        expListView = (ExpandableListView) findViewById(R.id.StockExpandList);

//        final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
//                this, groupList, laptopCollection);
//        expListView.setAdapter(expListAdapter);

    }

    private void createGroupList() {
        groupList = new ArrayList<String>();
        groupList.add("HP");
        groupList.add("Dell");
        groupList.add("Lenovo");
        groupList.add("Sony");
        groupList.add("HCL");
        groupList.add("Samsung");
    }

    private void createCollection() {
        // preparing laptops collection(child)
        String[] hpModels = { "HP Pavilion G6-2014TX", "ProBook HP 4540",
                "HP Envy 4-1025TX" };
        String[] hclModels = { "HCL S2101", "HCL L2102", "HCL V2002" };
        String[] lenovoModels = { "IdeaPad Z Series", "Essential G Series",
                "ThinkPad X Series", "Ideapad Z Series" };
        String[] sonyModels = { "VAIO E Series", "VAIO Z Series",
                "VAIO S Series", "VAIO YB Series" };
        String[] dellModels = { "Inspiron", "Vostro", "XPS" };
        String[] samsungModels = { "NP Series", "Series 5", "SF Series" };

        laptopCollection = new LinkedHashMap<String, List<String>>();

        for (String laptop : groupList) {
            if (laptop.equals("HP")) {
                loadChild(hpModels);
            } else if (laptop.equals("Dell"))
                loadChild(dellModels);
            else if (laptop.equals("Sony"))
                loadChild(sonyModels);
            else if (laptop.equals("HCL"))
                loadChild(hclModels);
            else if (laptop.equals("Samsung"))
                loadChild(samsungModels);
            else
                loadChild(lenovoModels);

            laptopCollection.put(laptop, childList);
        }
    }
    private void loadChild(String[] laptopModels) {
        childList = new ArrayList<String>();
        for (String model : laptopModels)
            childList.add(model);
    }

    private void setGroupIndicatorToRight() {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width
                - getDipsFromPixel(5));
    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
