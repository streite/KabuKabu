package com.mairyu.app.kabukabu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.mairyu.app.kabukabu.R.id.portfolioListViewPrice;

//==================================================================================================
//===   PortfolioView
//==================================================================================================
public class PortfolioView extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Stock> allStocks = new ArrayList<>();
    private ArrayAdapter<Stock> adapterStocks;

    private ListView listViewAllStocks;

    EditText edtPortfolioTicker;
    EditText edtPortfolioShares;
    EditText edtPortfolioBasis;
    EditText edtPortfolioComission;

    Button btnPortfolioAdd;
    Button btnDatabaseSave;
    Button btnDatabaseShow;
    Button btnDatabasePurge;

    private Toolbar mToolbar;

    private Spinner Portfolio_Spinner;

    private String[] CategoryArray;

    private PreferenceSettings _appPrefs;

    SQLhandler sqlHandler;
    String SQL_Filename;
    int SQLDBSize;
    String Category;
    String PortfolioCategory;

    float Price;
    int Shares;
    float GainLoss;
    float GainLossPerc;
    String ChangePerc;
    float Basis;
    int Comission;

    static final int SQL_POPUP_WIDTH = 1000;
    static final int SQL_POPUP_HEIGHT = 1500;
    static final int SQL_POPUP_GRAVITY_X = 0;
    static final int SQL_POPUP_GRAVITY_Y = 0;

    private PopupWindow popupWindow;

    static final int REQUEST_YAHOO = 1000;

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

        btnPortfolioAdd = (Button) findViewById(R.id.btnPortfolioAdd);
        btnPortfolioAdd.setOnClickListener(PortfolioView.this);

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

                allStocks = sqlHandler.getStocksByCategory(Category);

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

            DecimalFormat df1 = new DecimalFormat("#.#");
            DecimalFormat df2 = new DecimalFormat("#.##");

            Price = currentStock.getPrice();
            Shares = currentStock.getShares();
            ChangePerc = currentStock.getPercChange();
            Basis = currentStock.getBasis();
            Comission = currentStock.getCommission();

            //------------------------------------------------------------------------------------------
            TextView menuOption = (TextView) itemView.findViewById(R.id.portfolioListViewTicker);
            menuOption.setText(currentStock.getTicker());
            //------------------------------------------------------------------------------------------
            menuOption = (TextView) itemView.findViewById(portfolioListViewPrice);
            menuOption.setText(df1.format(currentStock.getPrice()));
            //------------------------------------------------------------------------------------------
            menuOption = (TextView) itemView.findViewById(R.id.portfolioListViewPercChange);
            if (currentStock.getPercChange().contains("+")) {
                menuOption.setTextColor(ContextCompat.getColor(PortfolioView.this, R.color.colorGreenStrong));
            } else {
                menuOption.setTextColor(ContextCompat.getColor(PortfolioView.this, R.color.colorRedStrong));
            }
            ChangePerc = ChangePerc.replace("%","");
            if (ChangePerc.equals("")) {
//                menuOption.setText(df1.format(Float.parseFloat(ChangePerc)) + "%");
            } else {
                menuOption.setText(df1.format(Float.parseFloat(ChangePerc)) + "%");
            }
            //------------------------------------------------------------------------------------------
            menuOption = (TextView) itemView.findViewById(R.id.portfolioListViewGainLoss);
            GainLoss = ((Price - Basis) * Shares) - Comission;
            if (GainLoss >= 0) {
                menuOption.setTextColor(ContextCompat.getColor(PortfolioView.this, R.color.colorGreenStrong));
            } else {
                menuOption.setTextColor(ContextCompat.getColor(PortfolioView.this, R.color.colorRedStrong));
            }
            menuOption.setText(df1.format(GainLoss));
            //------------------------------------------------------------------------------------------
            menuOption = (TextView) itemView.findViewById(R.id.portfolioListViewGainLossPerc);
            GainLossPerc = (GainLoss*100)/(Basis*Shares);
            if (GainLossPerc >= 0) {
                menuOption.setTextColor(ContextCompat.getColor(PortfolioView.this, R.color.colorGreenStrong));
            } else {
                menuOption.setTextColor(ContextCompat.getColor(PortfolioView.this, R.color.colorRedStrong));
            }
            menuOption.setText(df1.format(GainLossPerc) + "%");

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
            //---   ADD
            //--------------------------------------------------------------------------------------
            case R.id.btnPortfolioAdd:

                //----------------------------------------------------------------------------------
                //---   SQL File Selection Popup
                //----------------------------------------------------------------------------------

                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);

                View popupView = layoutInflater.inflate(R.layout.add_portfolio_popup, null);

                popupWindow = new PopupWindow(popupView, SQL_POPUP_WIDTH, SQL_POPUP_HEIGHT);

                // otherwise Keyboard doesn't pop up
                popupWindow.setFocusable(true);

                popupWindow.showAtLocation(findViewById(R.id.rlv_portfolio_view), Gravity.CENTER, SQL_POPUP_GRAVITY_X, SQL_POPUP_GRAVITY_Y);

                edtPortfolioTicker = (EditText) popupView.findViewById(R.id.edtPortfolioTicker);
//                edtPortfolioTicker.setFocusableInTouchMode(true);
//                edtPortfolioTicker.setFocusable(true);
//                edtPortfolioTicker.setCursorVisible(true);
//                edtPortfolioTicker.setClickable(true);
                edtPortfolioShares = (EditText) popupView.findViewById(R.id.edtPortfolioShares);
                edtPortfolioBasis = (EditText) popupView.findViewById(R.id.edtPortfolioBasis);
                edtPortfolioComission = (EditText) popupView.findViewById(R.id.edtPortfolioComission);

                btnPortfolioAdd = (Button) popupView.findViewById(R.id.btnPortfolioAdd);

                //----------------------------------------------------------------------------------
                //---   Spinner
                //----------------------------------------------------------------------------------
                Portfolio_Spinner = (Spinner) popupView.findViewById(R.id.spnPortfolioCategory);
                Portfolio_Spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.categories, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                Portfolio_Spinner.setAdapter(adapter);

                //----------------------------------------------------------------------------------
                //---
                //----------------------------------------------------------------------------------
                btnPortfolioAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Stock newStock = new Stock();

                        newStock.setTicker(edtPortfolioTicker.getText().toString());
                        newStock.setGroup(PortfolioCategory);
                        newStock.setShares(Integer.parseInt(edtPortfolioShares.getText().toString()));
                        newStock.setBasis(Float.parseFloat(edtPortfolioBasis.getText().toString()));
                        newStock.setCommission(Integer.parseInt(edtPortfolioComission.getText().toString()));

                        sqlHandler.addStock(newStock);

                        popupWindow.dismiss();
                    }
                });

                break;

        }
    }

    //**********************************************************************************************
    //***   CustomOnItemSelectedListener (for Spinner)
    //**********************************************************************************************
    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
            Toast.makeText(parent.getContext(),
                    "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
                    Toast.LENGTH_SHORT).show();

            PortfolioCategory = parent.getItemAtPosition(pos).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
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
        Stock currentStock = allStocks.get(info.position);

        switch (Title.toString()) {

            //--------------------------------------------------------------------------------------
            //---   EDIT
            //--------------------------------------------------------------------------------------
            case "Edit":

                Intent intent2Info = new Intent(PortfolioView.this, StockInfo.class);

                intent2Info.putExtra("SQL_STOCK_ID", currentStock.getId());

                //---   ==> CardView
                startActivity(intent2Info);

                break;
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

//        ImageView ivVectorImage = (ImageView) findViewById(R.id.action_refresh);
//        ImageButton ivVectorImage = (ImageButton) menu.findItem(R.id.action_refresh).getActionView();

//        ivVectorImage.setColorFilter(getResources().getColor(R.color.colorYellow1));

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
        if (id == R.id.action_refresh) {

            Intent intentYahoo = new Intent(PortfolioView.this, YahooAPI.class);
            ArrayList<String> TickerList = grabTickers();
            intentYahoo.putStringArrayListExtra("TICKER_INDEX_ARRAY", TickerList);
            startActivityForResult(intentYahoo,REQUEST_YAHOO);
        }

        return super.onOptionsItemSelected(item);
    }

    //**********************************************************************************************
    //***
    //**********************************************************************************************
    private ArrayList<String> grabTickers() {

        ArrayList<String> TickerList = new ArrayList<>();

        int index = 0;

        for (Stock tmpStock: allStocks) {

            TickerList.add(index++,tmpStock.getTicker());
        }

        return TickerList;
    }

    //**********************************************************************************************
    //***   onActivityResult (returning from Preferences)
    //**********************************************************************************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //------------------------------------------------------------------------------------------
        //---   Return from CardView
        //------------------------------------------------------------------------------------------
        if (requestCode == REQUEST_YAHOO) {

            //--------------------------------------------------------------------------------------
            //---   Return via finish()
            //--------------------------------------------------------------------------------------
            if (resultCode == Activity.RESULT_OK) {

                allStocks = sqlHandler.getStocksByCategory(Category);

                adapterStocks = new CustomDatabaseAdapter();
                listViewAllStocks.setAdapter(adapterStocks);
                adapterStocks.notifyDataSetChanged();
            }
        }
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