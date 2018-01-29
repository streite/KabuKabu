package com.mairyu.app.kabukabu;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

import static com.mairyu.app.kabukabu.R.id.menu_refresh;

//==================================================================================================
//===   IndexView
//==================================================================================================
public class IndexView extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] navigation;

    private ImageView usaFlag;
    private ImageView chinaFlag;
    private ImageView hongkongFlag;
    private ImageView taiwanFlag;
    private ImageView japanFlag;
    private ImageView koreaFlag;
    private ImageView indiaFlag;

    private TextView txtIndexViewUSA;
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

        usaFlag = (ImageView) findViewById(R.id.usaFlag);
        chinaFlag = (ImageView) findViewById(R.id.chinaFlag);
        hongkongFlag = (ImageView) findViewById(R.id.hongkongFlag);
        taiwanFlag = (ImageView) findViewById(R.id.taiwanFlag);
        japanFlag = (ImageView) findViewById(R.id.japanFlag);
        koreaFlag = (ImageView) findViewById(R.id.koreaFlag);
        indiaFlag = (ImageView) findViewById(R.id.indiaFlag);

        txtIndexViewUSA = (TextView) findViewById(R.id.txtIndexViewUSA);
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

                Resources res = getResources();

                HashMap<String, String> hashMap = (HashMap<String, String>) data.getSerializableExtra("HASH_MAP");

                displayIndexChange(res,usaFlag,txtIndexViewUSA,res.getDrawable(R.drawable.usa),hashMap,"Dow 30");

                displayIndexChange(res,chinaFlag,txtIndexViewChina,res.getDrawable(R.drawable.china),hashMap,"Shanghai");

                displayIndexChange(res,hongkongFlag,txtIndexViewHongKong,res.getDrawable(R.drawable.hongkong),hashMap,"Hang Seng");

                displayIndexChange(res,taiwanFlag,txtIndexViewTaiwan,res.getDrawable(R.drawable.taiwan),hashMap,"Taiwan Weighted");

                displayIndexChange(res,japanFlag,txtIndexViewJapan,res.getDrawable(R.drawable.japan),hashMap,"Nikkei 225");

                displayIndexChange(res,koreaFlag,txtIndexViewKorea,res.getDrawable(R.drawable.korea),hashMap,"KOSPI");

                displayIndexChange(res,indiaFlag,txtIndexViewIndia,res.getDrawable(R.drawable.india),hashMap,"BSE Sensex");
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
        menu_refresh.setVisible(true);

        MenuItem menu_undo = menu.findItem(R.id.menu_undo);
        menu_undo.setVisible(false);

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

        //------------------------------------------------------------------------------------------
        //---   Refresh Quotes -> Call API
        //------------------------------------------------------------------------------------------
        if (id == menu_refresh) {

            Intent intentInvesting = new Intent(IndexView.this, InvestingAPI.class);
            startActivityForResult(intentInvesting, REQUEST_INVESTING);
        }

        return super.onOptionsItemSelected(item);
    }

    //**********************************************************************************************
    //***   Method: Display Index Change
    //**********************************************************************************************
    public void displayIndexChange(Resources r, ImageView imgView, TextView txtView, Drawable flag, HashMap<String, String> hashMap, String Index){

        String Change = hashMap.get(Index);
        String OldChange = txtView.getText().toString().replace("%","");

        Drawable[] layers = new Drawable[2];
        layers[0] = flag;
        if (Float.parseFloat(Change) < Float.parseFloat(OldChange)) {
            layers[1] = r.getDrawable(R.drawable.ic_down_red);
            layers[1].setAlpha(250);
        } else if (Float.parseFloat(Change) > Float.parseFloat(OldChange)) {
            layers[1] = r.getDrawable(R.drawable.ic_up_green);
            layers[1].setAlpha(200);
        } else {
            layers[1] = r.getDrawable(R.drawable.ic_up_green);
            layers[1].setAlpha(0);
        }

        LayerDrawable layerDrawable = new LayerDrawable(layers);
        imgView.setImageDrawable(layerDrawable);

        txtView.setText(Change +"%");
        if (Float.parseFloat(hashMap.get(Index))<0) {
            txtView.setTextColor(ContextCompat.getColor(IndexView.this, R.color.colorRedStrong));
        } else {
            txtView.setTextColor(ContextCompat.getColor(IndexView.this, R.color.colorGreenStrong));
        }
    }
}
