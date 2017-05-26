package com.mairyu.app.kabukabu;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

//==================================================================================================
//===   PortfolioView
//==================================================================================================
public class PortfolioView extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Stock> allStocks = new ArrayList<>();
    private ArrayAdapter<Stock> adapterStocks;

    private ListView listViewAllStocks;

    Button btnDatabaseLoad;
    Button btnDatabaseSave;
    Button btnDatabaseShow;
    Button btnDatabasePurge;

    private Toolbar mToolbar;

    private String[] CategoryArray;

    private PreferenceSettings _appPrefs;

    SQLhandler sqlHandler;
    String SQL_Filename;
    int SQLDBSize;
    String Category;

    //**********************************************************************************************
    //***   onCreate
    //**********************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.portfolio_view);

        Bundle extras = getIntent().getExtras();
        Category = extras.getString("PORTFOLIO_CATEGORY");

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

        //------------------------------------------------------------------------------------------
        //---   Layout
        //------------------------------------------------------------------------------------------

        btnDatabaseLoad = (Button) findViewById(R.id.btnDatabaseLoad);
        btnDatabaseLoad.setOnClickListener(PortfolioView.this);

        btnDatabaseSave = (Button) findViewById(R.id.btnDatabaseSave);
        btnDatabaseSave.setOnClickListener(PortfolioView.this);

        btnDatabaseShow = (Button) findViewById(R.id.btnDatabaseShow);
        btnDatabaseShow.setOnClickListener(PortfolioView.this);

        btnDatabasePurge = (Button) findViewById(R.id.btnDatabasePurge);
        btnDatabasePurge.setOnClickListener(PortfolioView.this);

        //------------------------------------------------------------------------------------------
        //---   ListView Setup
        //------------------------------------------------------------------------------------------

        listViewAllStocks = (ListView) findViewById(R.id.listViewAllStocks);

        registerForContextMenu(listViewAllStocks);

        //------------------------------------------------------------------------------------------
        //---   SQLite Setup
        //------------------------------------------------------------------------------------------

        sqlHandler = new SQLhandler(PortfolioView.this, _appPrefs.getSQLDBName(), Integer.parseInt(_appPrefs.getSQLDBVersion()));

        //------------------------------------------------------------------------------------------
        //---   If cards are already loaded, show right away (wait until activity is stable)
        //------------------------------------------------------------------------------------------

        findViewById(R.id.rlv_portfolio_view).post(new Runnable() {

            public void run() {

                allStocks = sqlHandler.getStocksByTicker(Category);

                adapterStocks = new CustomDatabaseAdapter();
                listViewAllStocks.setAdapter(adapterStocks);

                SQLDBSize = sqlHandler.getStockCount();
            }
        });
    }

    //**********************************************************************************************
    //***   Custom Array Adapter for PortfolioPage
    //**********************************************************************************************
    private class CustomDatabaseAdapter extends ArrayAdapter<Stock> {

        public CustomDatabaseAdapter() {

            super(PortfolioView.this, R.layout.portfolio_list_view_item, allStocks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View itemView = convertView;

            if (itemView == null) {

                itemView = getLayoutInflater().inflate(R.layout.portfolio_list_view_item, parent, false);
            }

            Stock currentStock = allStocks.get(position);

            TextView menuOption = (TextView) itemView.findViewById(R.id.portfolioListViewTicker);
            menuOption.setText(currentStock.getTicker());

            menuOption = (TextView) itemView.findViewById(R.id.portfolioListViewPrice);
            menuOption.setText(currentStock.getPrice() + "");

            return itemView;
        }
    }

    //**********************************************************************************************
    //***   Control of bottom set of Buttons
    //**********************************************************************************************
    @Override
    public void onClick(View view) {

        //------------------------------------------------------------------------------------------
        //---   Setup Layout
        //------------------------------------------------------------------------------------------

        switch (view.getId()) {

            //--------------------------------------------------------------------------------------
            //---   SAVE
            //--------------------------------------------------------------------------------------
            case R.id.btnDatabaseSave:

                break;
        }
    }

    //**********************************************************************************************
    //***   onCreateContextMenu (Context Menu)
    //**********************************************************************************************
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Select The Action");

        menu.add(0, v.getId(), 0, "Add");
        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 0, "Delete");
    }

    //**********************************************************************************************
    //***   onContextItemSelected (Context Menu)
    //**********************************************************************************************
    @Override
    public boolean onContextItemSelected(MenuItem item){

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        CharSequence Title = item.getTitle();
        View view;

        switch (Title.toString()) {

        }

        return true;
    }

    //**********************************************************************************************
    //***   onCreateOptionsMenu (Toolbar)
    //**********************************************************************************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        String SQLFileName = "0";

        //------------------------------------------------------------------------------------------
        //---   Show active SQL DB
        //------------------------------------------------------------------------------------------

        TextView txtCategory = (TextView) findViewById(R.id.txtCategory);
        txtCategory.setText(Category+" ("+SQLDBSize+")");
//        txtCategory.setTextColor(getResources().getColor(R.color.colorYellow1));
        txtCategory.setTextColor(ContextCompat.getColor(this, R.color.colorYellow1));

        return true;
    }

    //**********************************************************************************************
    //***   onOptionsItemSelected (Toolbar)
    //**********************************************************************************************
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //------------------------------------------------------------------------------------------
        //---   Pull down 'Settings' Menu
        //------------------------------------------------------------------------------------------
//        if (id == R.id.action_settings) {
//
//            Intent intentSettings = new Intent(DatabasePage.this, SettingsPage.class);
//            startActivityForResult(intentSettings,REQUEST_PREFERENCE);
//        }

        return super.onOptionsItemSelected(item);
    }

    //**********************************************************************************************
    //***   CustomOnItemSelectedListener (for Spinner)
    //**********************************************************************************************
//    public class CustomOnItemSelectedListener implements OnItemSelectedListener {
//
//        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//            Toast.makeText(parent.getContext(),
//                    "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
//                    Toast.LENGTH_SHORT).show();
//
//            Google_Sheet = parent.getItemAtPosition(pos).toString();
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> arg0) {
//            // TODO Auto-generated method stub
//        }
//    }
}