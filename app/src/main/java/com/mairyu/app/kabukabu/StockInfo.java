package com.mairyu.app.kabukabu;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static android.widget.AdapterView.OnItemSelectedListener;

//==================================================================================================
//===   StockInfo
//==================================================================================================
public class StockInfo extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;

    private PreferenceSettings _appPrefs;

    SQLhandler sqlHandler;
    int SQL_Stock_ID;

    DateFormat df = new SimpleDateFormat("MM-dd-yyyy");

    EditText edtStockInfoCompany;
    EditText edtStockInfoTicker;
    EditText edtStockInfoShares;
    EditText edtStockInfoBasis;
    EditText edtStockInfoComission;
    EditText edtStockInfoLeverage;
    EditText edtStockInfoDate;

    String PortfolioCategory;
    int Category_Index;

    float OldBasis;

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

        sqlHandler = new SQLhandler(StockInfo.this,_appPrefs.getSQLStockDBName(),
                Integer.parseInt(_appPrefs.getSQLStockDBVersion()));

        //------------------------------------------------------------------------------------------
        //---   Layout
        //------------------------------------------------------------------------------------------

        edtStockInfoTicker = (EditText) findViewById(R.id.edtStockInfoTicker);
        edtStockInfoCompany = (EditText) findViewById(R.id.edtStockInfoCompany);
        edtStockInfoShares = (EditText) findViewById(R.id.edtStockInfoShares);
        edtStockInfoShares.setOnClickListener(this);
        edtStockInfoBasis = (EditText) findViewById(R.id.edtStockInfoBasis);
        edtStockInfoBasis.setOnClickListener(this);
        edtStockInfoComission = (EditText) findViewById(R.id.edtStockInfoComission);
        edtStockInfoLeverage = (EditText) findViewById(R.id.edtStockInfoLeverage);
        edtStockInfoDate = (EditText) findViewById(R.id.edtStockInfoDate);

        //----------------------------------------------------------------------------------
        //---   Spinner
        //----------------------------------------------------------------------------------

        CategoryArray = getResources().getStringArray(R.array.categories);

//        Portfolio_Spinner = (Spinner) findViewById(R.id.spnStockInfoCategory);
//        Portfolio_Spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.categories, android.R.layout.simple_spinner_item);
//// Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//// Apply the adapter to the spinner
//        Portfolio_Spinner.setAdapter(adapter);

//        Spinner mySpinner = (Spinner)findViewById(R.id.spnStockInfoCategory);
//        mySpinner.setAdapter(new MyCustomAdapter(StockInfo.this, R.layout.row, CategoryArray));
//        mySpinner.setSelection(0);

        //------------------------------------------------------------------------------------------
        //---   Wait until activity is stable
        //------------------------------------------------------------------------------------------
        findViewById(R.id.rlv_stock_info).post(new Runnable() {

            public void run() {

                Stock tmpStock = sqlHandler.getStockByID(SQL_Stock_ID);

                edtStockInfoTicker.setText(tmpStock.getTicker());
                edtStockInfoCompany.setText("("+tmpStock.getCompany()+")");
                edtStockInfoShares.setText(tmpStock.getShares()+"");
                edtStockInfoBasis.setText(tmpStock.getBasis()+"");
                edtStockInfoComission.setText(tmpStock.getCommission()+"");
                edtStockInfoLeverage.setText(tmpStock.getLeverage()+"");
                edtStockInfoDate.setText(tmpStock.getDate()+"");

                OldBasis = tmpStock.getBasis();

                editOn();
            }
        });
    }

    //**********************************************************************************************
    //***   onClick (Button)
    //**********************************************************************************************
    @Override
    public void onClick(View view) {

        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        //------------------------------------------------------------------------------------------
        //---   Setup Layout
        //------------------------------------------------------------------------------------------

        switch (view.getId()) {

            //--------------------------------------------------------------------------------------
            //---   Shares
            //--------------------------------------------------------------------------------------
            case R.id.edtStockInfoShares:

                edtStockInfoShares.setText("0");
                edtStockInfoShares.setFocusableInTouchMode(true);
                edtStockInfoShares.setFocusable(true);
                edtStockInfoShares.requestFocus();

//                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                break;

            //--------------------------------------------------------------------------------------
            //---   Basis
            //--------------------------------------------------------------------------------------
            case R.id.edtStockInfoBasis:

                edtStockInfoBasis.setText("");
                edtStockInfoBasis.getText().clear();
                edtStockInfoBasis.setFocusableInTouchMode(true);
                edtStockInfoBasis.setFocusable(true);
                edtStockInfoBasis.requestFocus();

//                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                break;

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

        MenuItem menu_refresh = menu.findItem(R.id.menu_refresh);
        menu_refresh.setVisible(false);

        //------------------------------------------------------------------------------------------
        //---   Pull-Down Menu for SQL DB
        //------------------------------------------------------------------------------------------

        MenuItem item = menu.findItem(R.id.menu_spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        String[] CategoryArray = getResources().getStringArray(R.array.categories);
//        String SheetTag = _appPrefs.getSQLFlashcardDBName().substring(11);
//        SheetTag = SheetTag.substring(0,SheetTag.length()-3);

        Stock tmpStock = sqlHandler.getStockByID(SQL_Stock_ID);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        adapter.setDropDownViewResource(android.R.layout.spinner_drop_down);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        spinner.setSelection(Arrays.asList(CategoryArray).indexOf(tmpStock.getCategory()));

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
        //---   Pull down 'Settings' Menu
        //------------------------------------------------------------------------------------------
        if (id == R.id.menu_refresh) {

//            Intent intentYahoo = new Intent(PortfolioView.this, YahooAPI.class);
//            ArrayList<String> TickerList = grabTickers();
//            intentYahoo.putStringArrayListExtra("TICKER_INDEX_ARRAY", TickerList);
//            startActivityForResult(intentYahoo,REQUEST_YAHOO);
        }

        //------------------------------------------------------------------------------------------
        //---   Update and return to Portfolio
        //------------------------------------------------------------------------------------------
        if (id == R.id.menu_edit) {

            Stock tmpStock = sqlHandler.getStockByID(SQL_Stock_ID);

            tmpStock.setTicker(edtStockInfoTicker.getText().toString());
            String Company = edtStockInfoCompany.getText().toString().replace("(","");
            Company = Company.replace(")","");
            tmpStock.setCompany(Company);
            tmpStock.setShares(Integer.parseInt(edtStockInfoShares.getText().toString()));
            tmpStock.setBasis(Float.parseFloat(edtStockInfoBasis.getText().toString()));
            tmpStock.setCommission(Integer.parseInt(edtStockInfoComission.getText().toString()));
            tmpStock.setLeverage(edtStockInfoLeverage.getText().toString());
            tmpStock.setCategory(PortfolioCategory);

            Calendar calendarNow = Calendar.getInstance();
            Date rightNow = calendarNow.getTime();
            tmpStock.setDate(df.format(rightNow));

            //--------------------------------------------------------------------------------------
            //---   Buy
            //--------------------------------------------------------------------------------------
            if (edtStockInfoShares.getText().toString()!="0") {

                tmpStock.setWatch(1);

            //--------------------------------------------------------------------------------------
            //---   Sale ...
            //--------------------------------------------------------------------------------------
            } else {

                //----------------------------------------------------------------------------------
                //---   ... w/ Loss
                //----------------------------------------------------------------------------------
                if (OldBasis > tmpStock.getBasis()) {

                    tmpStock.setWatch(3);

                //----------------------------------------------------------------------------------
                //---   ... w/ Gain
                //----------------------------------------------------------------------------------
                } else {

                    tmpStock.setWatch(2);
                }
            }

            sqlHandler.updateStock(tmpStock);

            hideKeyboard(this);

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

        edtStockInfoCompany.setFocusableInTouchMode(true);
        edtStockInfoCompany.setFocusable(true);
        edtStockInfoCompany.setCursorVisible(true);
        edtStockInfoCompany.setClickable(true);

//        edtStockInfoShares.setFocusableInTouchMode(true);
//        edtStockInfoShares.setFocusable(true);
        edtStockInfoShares.setCursorVisible(true);
        edtStockInfoShares.setClickable(true);

//        edtStockInfoBasis.setFocusableInTouchMode(true);
//        edtStockInfoBasis.setFocusable(true);
        edtStockInfoBasis.setCursorVisible(true);
        edtStockInfoBasis.setClickable(true);

        edtStockInfoComission.setFocusableInTouchMode(true);
        edtStockInfoComission.setFocusable(true);
        edtStockInfoComission.setCursorVisible(true);
        edtStockInfoComission.setClickable(true);


        edtStockInfoLeverage.setFocusableInTouchMode(true);
        edtStockInfoLeverage.setFocusable(true);
        edtStockInfoLeverage.setCursorVisible(true);
        edtStockInfoLeverage.setClickable(true);
    }

    //**********************************************************************************************
    //***   CustomOnItemSelectedListener (for Spinner)
    //**********************************************************************************************
    public class CustomOnItemSelectedListener implements OnItemSelectedListener {

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            Toast.makeText(parent.getContext(),
                    "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
                    Toast.LENGTH_SHORT).show();

            PortfolioCategory = parent.getItemAtPosition(pos).toString();

//            Google_Sheet = parent.getItemAtPosition(pos).toString();
//
//            String[] CategoriesArray = getResources().getStringArray(R.array.categories);
////                ArrayList<String> SheetNames = new ArrayList<String>(SheetNamesArray);
//
//            String SQLFileName = "lingoflash." + Google_Sheet + ".db";
//            String Language = Google_Sheet.substring(0,2);
//
//            _appPrefs.setSQLFlashcardDBName(SQLFileName);
//            _appPrefs.setLanguage(Language);
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

    //**********************************************************************************************
    //***   Method: Hide Keyboard
    //**********************************************************************************************
    public static void hideKeyboard(Activity activity) {

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();

        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
