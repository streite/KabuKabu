package com.mairyu.app.kabukabu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

//==================================================================================================
//===   IndexView
//==================================================================================================
public class IndexView extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] navigation;

    private TextView txtIndexViewChina;

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

                txtIndexViewChina.setText("HELLO");
                txtIndexViewChina.setTextColor(ContextCompat.getColor(IndexView.this, R.color.colorRedStrong));

            }
        });

        Intent intentInvesting = new Intent(IndexView.this, InvestingAPI.class);
        startActivityForResult(intentInvesting, REQUEST_INVESTING);
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
}
