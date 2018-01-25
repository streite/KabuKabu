package com.mairyu.app.kabukabu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

//==================================================================================================
//===   IndexView
//==================================================================================================
public class IndexView extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] navigation;

    private TextView txtIndexViewChina;
    private TextView txtIndexViewHongKong;
    private TextView txtIndexViewTaiwan;
    private TextView txtIndexViewJapan;
    private TextView txtIndexViewKorea;
    private TextView txtIndexViewIndia;

    GridLayout glIndexView;

    static final int REQUEST_INFO = 1000;
    static final int REQUEST_YAHOO = 2000;
    static final int REQUEST_YAHOO2 = 2001;
    static final int REQUEST_YAHOO3 = 2002;
    static final int REQUEST_YAHOO4 = 2003;
    static final int REQUEST_INVESTING = 5000;

//    https://www.investing.com/indices/world-indices

    //**********************************************************************************************
    //***   onCreate
    //**********************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.index_view);

        //----------------------------------------------------------------------------------
        //---   Layout
        //----------------------------------------------------------------------------------

        txtIndexViewChina = (TextView) findViewById(R.id.txtIndexViewChina);
        txtIndexViewHongKong = (TextView) findViewById(R.id.txtIndexViewHongKong);
        txtIndexViewTaiwan = (TextView) findViewById(R.id.txtIndexViewTaiwan);
        txtIndexViewJapan = (TextView) findViewById(R.id.txtIndexViewJapan);
        txtIndexViewKorea = (TextView) findViewById(R.id.txtIndexViewKorea);
        txtIndexViewIndia = (TextView) findViewById(R.id.txtIndexViewIndia);

        //----------------------------------------------------------------------------------
        //---   Navigation Drawer
        //----------------------------------------------------------------------------------

        navigation = getResources().getStringArray(R.array.navigation);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,navigation));
        mDrawerList.setOnItemClickListener(new Navigator(this));

        //------------------------------------------------------------------------------------------
        //---   GridView Setup
        //------------------------------------------------------------------------------------------

        glIndexView = (GridLayout) findViewById(R.id.glIndexView);

        //------------------------------------------------------------------------------------------
        //---   Show initial card
        //------------------------------------------------------------------------------------------

        RelativeLayout rlhIndexView = (RelativeLayout) findViewById(R.id.rlhIndexView);

        rlhIndexView.post(new Runnable() {

            public void run() {

//                txtIndexViewChina.setText("HELLO");
//                txtIndexViewChina.setTextColor(ContextCompat.getColor(IndexView.this, R.color.colorRedStrong));

            }
        });

        Intent intentInvesting = new Intent(IndexView.this, InvestingAPI.class);
        startActivityForResult(intentInvesting, REQUEST_INVESTING);
    }

    //**********************************************************************************************
    //***   onActivityResult (returning from InvestingAPI)
    //**********************************************************************************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //------------------------------------------------------------------------------------------
        //---   Return from CardView
        //------------------------------------------------------------------------------------------
        if ((requestCode == REQUEST_INVESTING)) {

            //--------------------------------------------------------------------------------------
            //---   Return via finish()
            //--------------------------------------------------------------------------------------
            if (resultCode == Activity.RESULT_OK) {

                HashMap<String, String> hashMap = (HashMap<String, String>) data.getSerializableExtra("HASH_MAP");

                txtIndexViewChina.setText(hashMap.get("Shanghai")+"%");
                if (Float.parseFloat(hashMap.get("Shanghai"))<0) {
                    txtIndexViewChina.setTextColor(ContextCompat.getColor(IndexView.this, R.color.colorRedStrong));
                } else {
                    txtIndexViewChina.setTextColor(ContextCompat.getColor(IndexView.this, R.color.colorGreenStrong));
                }

                displayIndexChange(txtIndexViewHongKong,hashMap,"Hang Seng");

                displayIndexChange(txtIndexViewTaiwan,hashMap,"Taiwan Weighted");

                txtIndexViewJapan.setText(hashMap.get("Nikkei 225")+"%");
                if (Float.parseFloat(hashMap.get("Nikkei 225"))<0) {
                    txtIndexViewJapan.setTextColor(ContextCompat.getColor(IndexView.this, R.color.colorRedStrong));
                } else {
                    txtIndexViewJapan.setTextColor(ContextCompat.getColor(IndexView.this, R.color.colorGreenStrong));
                }

                txtIndexViewKorea.setText(hashMap.get("KOSPI")+"%");
                if (Float.parseFloat(hashMap.get("KOSPI"))<0) {
                    txtIndexViewKorea.setTextColor(ContextCompat.getColor(IndexView.this, R.color.colorRedStrong));
                } else {
                    txtIndexViewKorea.setTextColor(ContextCompat.getColor(IndexView.this, R.color.colorGreenStrong));
                }

                displayIndexChange(txtIndexViewIndia,hashMap,"BSE Sensex");
            }
        }
    }

    //**********************************************************************************************
    //***   onCreateOptionsMenu (Toolbar)
    //**********************************************************************************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),
                R.drawable.gradient_dark_grey_bg, null));

        MenuItem menu_spinner = menu.findItem(R.id.menu_spinner);
        menu_spinner.setVisible(false);

        MenuItem menu_edit = menu.findItem(R.id.menu_edit);
        menu_edit.setVisible(false);

        MenuItem menu_refresh = menu.findItem(R.id.menu_refresh);
        menu_refresh.setVisible(false);

        //------------------------------------------------------------------------------------------
        //---   Suppress Category Header
        //------------------------------------------------------------------------------------------

        TextView Category = (TextView) findViewById(R.id.txtCategory);
        Category.setVisibility(View.GONE);

        return true;
    }

    //**********************************************************************************************
    //***   onOptionsItemSelected (Toolbar)
    //**********************************************************************************************
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    //**********************************************************************************************
    //***   Method: Display Index Change
    //**********************************************************************************************
    public void displayIndexChange(TextView txtView, HashMap<String, String> hashMap, String Index){

        txtView.setText(hashMap.get(Index)+"%");
        if (Float.parseFloat(hashMap.get(Index))<0) {
            txtView.setTextColor(ContextCompat.getColor(IndexView.this, R.color.colorRedStrong));
        } else {
            txtView.setTextColor(ContextCompat.getColor(IndexView.this, R.color.colorGreenStrong));
        }
    }
}
