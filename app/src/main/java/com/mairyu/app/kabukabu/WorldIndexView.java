package com.mairyu.app.kabukabu;

import android.app.Activity;
import android.content.Context;
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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.mairyu.app.kabukabu.R.id.menu_refresh;

//==================================================================================================
//===   IndexView
//==================================================================================================
public class WorldIndexView extends AppCompatActivity {

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

    private ArrayList<Index> allFlashcards = new ArrayList<>();
    private ArrayList<Index> activeIndices = new ArrayList<>();
    private ArrayAdapter<Index> adapterAllIndices;

    private String[] IndexArray;
    private ArrayList<String> IndexArrayList;

    ListView lvWorldIndex;

    HashMap<String, String> mapCountry2Flag = new HashMap<String, String>();

    Calendar calendarNow;
    Date rightNow;

    private boolean ViewExpand = false;
    private int ViewExpandPosition;

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
        setContentView(R.layout.world_index_view);

        //----------------------------------------------------------------------------------
        //---   Look-up Table
        //----------------------------------------------------------------------------------

        String[] countriesNames = getResources().getStringArray(R.array.country_names);
        String[] imageNames = getResources().getStringArray(R.array.img_names);

        mapCountry2Flag = new HashMap<String, String>();

        for (int i = 0; i < countriesNames.length; i++) {

            mapCountry2Flag.put(countriesNames[i], imageNames[i]);
        }

        calendarNow = Calendar.getInstance();

        //----------------------------------------------------------------------------------
        //---   Layout
        //----------------------------------------------------------------------------------

//        usaFlag = (ImageView) findViewById(R.id.usaFlag);
//        chinaFlag = (ImageView) findViewById(R.id.chinaFlag);
//        hongkongFlag = (ImageView) findViewById(R.id.hongkongFlag);
//        taiwanFlag = (ImageView) findViewById(R.id.taiwanFlag);
//        japanFlag = (ImageView) findViewById(R.id.japanFlag);
//        koreaFlag = (ImageView) findViewById(R.id.koreaFlag);
//        indiaFlag = (ImageView) findViewById(R.id.indiaFlag);

//        txtIndexViewUSA = (TextView) findViewById(R.id.txtIndexViewUSA);
//        txtIndexViewChina = (TextView) findViewById(R.id.txtIndexViewChina);
//        txtIndexViewHongKong = (TextView) findViewById(R.id.txtIndexViewHongKong);
//        txtIndexViewTaiwan = (TextView) findViewById(R.id.txtIndexViewTaiwan);
//        txtIndexViewJapan = (TextView) findViewById(R.id.txtIndexViewJapan);
//        txtIndexViewKorea = (TextView) findViewById(R.id.txtIndexViewKorea);
//        txtIndexViewIndia = (TextView) findViewById(R.id.txtIndexViewIndia);

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

        lvWorldIndex = (ListView) findViewById(R.id.lvWorldIndex);

        //------------------------------------------------------------------------------------------
        //---   from 'strings.xml'
        //------------------------------------------------------------------------------------------

        IndexArray = getResources().getStringArray(R.array.world_indices);
        IndexArrayList = new ArrayList<String>(Arrays.asList(IndexArray));

        //------------------------------------------------------------------------------------------
        //---   If cards are already loaded, show right away (wait until activity is stable)
        //------------------------------------------------------------------------------------------

        findViewById(R.id.rlhWorldIndexView).post(new Runnable() {

            public void run() {

                adapterAllIndices = new CustomAllCardsAdapter(WorldIndexView.this, R.layout.world_index_list_view_item, activeIndices);
                lvWorldIndex.setAdapter(adapterAllIndices);
            }
        });

        Intent intentInvesting = new Intent(WorldIndexView.this, InvestingAPI.class);
        startActivityForResult(intentInvesting, REQUEST_INVESTING);
    }

    //**********************************************************************************************
    //***   Custom Array Adapter for List of active Flashcards
    //**********************************************************************************************
    private class CustomAllCardsAdapter extends ArrayAdapter<Index> {

        private final Context context;
        private ArrayList<Index> events;

        //------------------------------------------------------------------------------------------
        //---   Constructor
        //------------------------------------------------------------------------------------------
        public CustomAllCardsAdapter(Context context, int layoutResourceId, ArrayList<Index> events) {

            super(context, layoutResourceId, activeIndices);

            this.context = context;
            this.events = events;
        }

        //------------------------------------------------------------------------------------------
        //---   'getView' actually fill the listview
        //------------------------------------------------------------------------------------------
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

//            String IndexName = IndexArrayList.get(position);

            Index currentIndex = activeIndices.get(position);

            //--------------------------------------------------------------------------------------
            //---   Recycle View
            //--------------------------------------------------------------------------------------

            if (convertView == null) {

                //----------------------------------------------------------------------------------
                //---   Select corresponding listview
                //----------------------------------------------------------------------------------

                if (ViewExpand && (ViewExpandPosition == position)) {

                    convertView = getLayoutInflater().inflate(R.layout.world_index_list_view_item, parent, false);

                } else {

                    convertView = getLayoutInflater().inflate(R.layout.world_index_list_view_item, parent, false);
                }
            }

            //--------------------------------------------------------------------------------------
            //---   Market Open/Closed
            //--------------------------------------------------------------------------------------

//            checkMarketOpen("japan");

            RelativeLayout rlhWorldIndex = (RelativeLayout) convertView.findViewById(R.id.rlhWorldIndex);

            if (currentIndex.getMarketOpen() == 1) {
                rlhWorldIndex.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.gradient_light_yellow_bg, null));
            } else {
                rlhWorldIndex.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.gradient_dark_yellow_bg, null));
            }

            //--------------------------------------------------------------------------------------
            //---   Flag
            //--------------------------------------------------------------------------------------

            int drawableId = getResources().getIdentifier(mapCountry2Flag.get(currentIndex.getCountry()), "drawable", getPackageName());

            ImageView indexFlag = (ImageView) convertView.findViewById(R.id.imvWorldIndexFlag);
//            indexFlag.setImageResource(R.drawable.usa);
            indexFlag.setImageResource(drawableId);
            indexFlag.setVisibility(View.VISIBLE);

            //--------------------------------------------------------------------------------------
            //---   Index Name
            //--------------------------------------------------------------------------------------

            TextView indexName = (TextView) convertView.findViewById(R.id.txtWorldIndexName);
            indexName.setText(currentIndex.getName()+"");
            indexName.setVisibility(View.VISIBLE);

            //--------------------------------------------------------------------------------------
            //---   Gain/Loss (%)
            //--------------------------------------------------------------------------------------

            Float PercChange = currentIndex.getPercChange();
            TextView indexPercChange = (TextView) convertView.findViewById(R.id.txtWorldIndexPercChange);
            indexPercChange.setText(PercChange + "%");

            if (PercChange < 0) {
                indexPercChange.setTextColor(ContextCompat.getColor(WorldIndexView.this, R.color.colorRedStrong));
            } else if (PercChange > 0) {
                indexPercChange.setTextColor(ContextCompat.getColor(WorldIndexView.this, R.color.colorGreenStrong));
            } else {
                indexPercChange.setTextColor(ContextCompat.getColor(WorldIndexView.this, R.color.colorGrey1));
            }
            indexPercChange.setVisibility(View.VISIBLE);

            //--------------------------------------------------------------------------------------
            //---   Trend
            //--------------------------------------------------------------------------------------

            return convertView;
        }
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

                allFlashcards = data.getParcelableArrayListExtra("INDEX_CHANGE_ARRAY");

                activeIndices.clear();

                HashMap<String, Index> indexMap = new HashMap<String, Index>();
                for (Index index : allFlashcards) {
                    indexMap.put(index.getName(), index);
                }

                for (String indexName : IndexArray) {
                    indexName = indexName.replace("S_P", "S&P");
                    activeIndices.add(indexMap.get(indexName));
                }

                adapterAllIndices = new CustomAllCardsAdapter(WorldIndexView.this, R.layout.world_index_list_view_item, activeIndices);
                lvWorldIndex.setAdapter(adapterAllIndices);
                adapterAllIndices.notifyDataSetChanged();

//                Resources res = getResources();
//
//                displayIndexChange(res,indiaFlag,txtIndexViewIndia,res.getDrawable(R.drawable.india),hashMap,"BSE Sensex");
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

//        TextView Category = (TextView) findViewById(R.id.txtCategory);
//        Category.setVisibility(View.GONE);

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

            Intent intentInvesting = new Intent(WorldIndexView.this, InvestingAPI.class);
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
            txtView.setTextColor(ContextCompat.getColor(WorldIndexView.this, R.color.colorRedStrong));
        } else {
            txtView.setTextColor(ContextCompat.getColor(WorldIndexView.this, R.color.colorGreenStrong));
        }
    }

    //**********************************************************************************************
    //***   Method: Check whether market is open
    //**********************************************************************************************
    public boolean checkMarketOpen(String Market){

        switch (Market) {

            case "japan": {

                if ( checkTimeRange ("4:00","22:00")) { return (true); }

                break;
            }
        }

        return (false);
    }

    //**********************************************************************************************
    //***   Method: Check whether market is open
    //**********************************************************************************************
    public boolean checkTimeRange(String Open, String Close){

        int hour = calendarNow.get(Calendar.HOUR);
        int minute = calendarNow.get(Calendar.MINUTE);

        SimpleDateFormat inputParser = new SimpleDateFormat("HH:mm", Locale.US);

        Date MarketClose;
        Date MarketOpen;

        try {

            Date rightNow = inputParser.parse(hour + ":" + minute);
            MarketOpen = inputParser.parse(Open);
            MarketClose = inputParser.parse(Close);

            if (MarketOpen.before(rightNow) && MarketClose.after(rightNow)) { return (true); }

        } catch (java.text.ParseException e) {
        }

        return (false);
    }
}
