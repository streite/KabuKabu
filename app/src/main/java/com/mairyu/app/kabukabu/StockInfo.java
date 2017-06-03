package com.mairyu.app.kabukabu;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//==================================================================================================
//===   StockInfo
//==================================================================================================
public class StockInfo extends AppCompatActivity {

    private Toolbar mToolbar;

    private PreferenceSettings _appPrefs;

    SQLhandler sqlHandler;
    int SQL_Stock_ID;

    EditText edtStockInfoTicker;
    EditText edtStockInfoShares;
    EditText edtStockInfoBasis;
    EditText edtStockInfoComission;

    String PortfolioCategory;
    int Category_Index;

    private Spinner Portfolio_Spinner;

    private String[] CategoryArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_info);

        Bundle extras = getIntent().getExtras();
        SQL_Stock_ID = extras.getInt("SQL_STOCK_ID");
        Category_Index = extras.getInt("CATEGORY_INDEX");

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //------------------------------------------------------------------------------------------
        //---   SQLite Setup
        //------------------------------------------------------------------------------------------

        sqlHandler = new SQLhandler(StockInfo.this, _appPrefs.getSQLDBName(), Integer.parseInt(_appPrefs.getSQLDBVersion()));

        //------------------------------------------------------------------------------------------
        //---   Layout
        //------------------------------------------------------------------------------------------

        edtStockInfoTicker = (EditText) findViewById(R.id.edtStockInfoTicker);
        edtStockInfoShares = (EditText) findViewById(R.id.edtStockInfoShares);
        edtStockInfoBasis = (EditText) findViewById(R.id.edtStockInfoBasis);
        edtStockInfoComission = (EditText) findViewById(R.id.edtStockInfoComission);

        //----------------------------------------------------------------------------------
        //---   Spinner
        //----------------------------------------------------------------------------------

        CategoryArray = getResources().getStringArray(R.array.categories);

        Portfolio_Spinner = (Spinner) findViewById(R.id.spnStockInfoCategory);
        Portfolio_Spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        Portfolio_Spinner.setAdapter(adapter);

        Spinner mySpinner = (Spinner)findViewById(R.id.spnStockInfoCategory);
        mySpinner.setAdapter(new MyCustomAdapter(StockInfo.this, R.layout.row, CategoryArray));

    //------------------------------------------------------------------------------------------
        //---   Wait until activity is stable
        //------------------------------------------------------------------------------------------
        findViewById(R.id.rlv_stock_info).post(new Runnable() {

            public void run() {

                Stock tmpStock = sqlHandler.getStockByID(SQL_Stock_ID);

                edtStockInfoTicker.setText(tmpStock.getTicker());
                edtStockInfoShares.setText(tmpStock.getShares()+"");
                edtStockInfoBasis.setText(tmpStock.getBasis()+"");
                edtStockInfoComission.setText(tmpStock.getCommission()+"");

                editOn();
            }
        });
    }

    //**********************************************************************************************
    //***   onCreateOptionsMenu (Toolbar)
    //**********************************************************************************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

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

//            Intent intentYahoo = new Intent(PortfolioView.this, YahooAPI.class);
//            ArrayList<String> TickerList = grabTickers();
//            intentYahoo.putStringArrayListExtra("TICKER_INDEX_ARRAY", TickerList);
//            startActivityForResult(intentYahoo,REQUEST_YAHOO);
        }

        //------------------------------------------------------------------------------------------
        //---   Pull down 'Settings' Menu
        //------------------------------------------------------------------------------------------
        if (id == R.id.action_edit) {

            Stock tmpStock = sqlHandler.getStockByID(SQL_Stock_ID);

            tmpStock.setTicker(edtStockInfoTicker.getText().toString());
            tmpStock.setShares(Integer.parseInt(edtStockInfoShares.getText().toString()));
            tmpStock.setBasis(Float.parseFloat(edtStockInfoBasis.getText().toString()));
            tmpStock.setCommission(Integer.parseInt(edtStockInfoComission.getText().toString()));
            tmpStock.setGroup(PortfolioCategory);

            sqlHandler.updateStock(tmpStock);

            setResult(Activity.RESULT_OK);

            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    //**********************************************************************************************
    //***   Method: Edit Mode on
    //**********************************************************************************************
    public void editOn () {

        edtStockInfoTicker.setFocusableInTouchMode(true);
        edtStockInfoTicker.setFocusable(true);
        edtStockInfoTicker.setCursorVisible(true);
        edtStockInfoTicker.setClickable(true);
        edtStockInfoTicker.setOnTouchListener(null);

        edtStockInfoShares.setFocusableInTouchMode(true);
        edtStockInfoShares.setFocusable(true);
        edtStockInfoShares.setCursorVisible(true);
        edtStockInfoShares.setClickable(true);

        edtStockInfoBasis.setFocusableInTouchMode(true);
        edtStockInfoBasis.setFocusable(true);
        edtStockInfoBasis.setCursorVisible(true);
        edtStockInfoBasis.setClickable(true);

        edtStockInfoComission.setFocusableInTouchMode(true);
        edtStockInfoComission.setFocusable(true);
        edtStockInfoComission.setCursorVisible(true);
        edtStockInfoComission.setClickable(true);
    }

    //**********************************************************************************************
    //***   CustomOnItemSelectedListener (for Spinner)
    //**********************************************************************************************
    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
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
    //***   Custom Array Adapter for PortfolioPage
    //**********************************************************************************************
    public class MyCustomAdapter extends ArrayAdapter<String>{

        public MyCustomAdapter(Context context, int textViewResourceId,
                               String[] objects) {
            super(context, textViewResourceId, objects);

            // TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            // TODO Auto-generated method stub
//            View row;
//            LayoutInflater inflater=getLayoutInflater();
//            row = inflater.inflate(_resource, null);
//            TextView _textView = row.findViewById(_textViewResourceId);
//            _textView.setText(_navigations.get(position));
//
//
//            Display display = getWindowManager().getDefaultDisplay();
//            Point size = new Point();
//            display.getSize(size);
//            int _width = size.x;
//
//            row.setMinimumWidth = _width;
//            return row;

            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            //return super.getView(position, convertView, parent);

            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.row, parent, false);
            TextView label=(TextView)row.findViewById(R.id.weekofday);
            label.setText(CategoryArray[position]);

            return row;
        }
    }
}
