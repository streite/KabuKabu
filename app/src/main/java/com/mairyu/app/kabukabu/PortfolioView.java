package com.mairyu.app.kabukabu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.mairyu.app.kabukabu.R.id.menu_refresh;
import static com.mairyu.app.kabukabu.R.id.pager;
import static java.lang.StrictMath.abs;

//==================================================================================================
//===   PortfolioView
//==================================================================================================
public class PortfolioView extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Stock> allStocks = new ArrayList<>();
    private ArrayList<String> allSubcategories = new ArrayList<>();

    private ArrayAdapter<Stock> adapterStocks;
    private ExpandableListAdapter expListAdapter;
    private ArrayAdapter<String> Portfolio_SubCategory_Adapter;

    EditText edtPortfolioTicker;
    EditText edtPortfolioShares;
    EditText edtPortfolioBasis;
    EditText edtPortfolioComission;

    private Button btnPortfolioAdd;
    private Button btnDatabaseSave;
    private Button btnDatabaseShow;
    private Button btnPortfolioFilterShares;
    private Button btnPortfolioFilterPerc;

    private TextView portfolioListViewChangePerc;

    private Toolbar mToolbar;

    private Spinner Portfolio_Spinner;
    private Spinner Portfolio_SubCategory_Spinner;

    private String[] CategoryArray;
    private String[] SubCategoryArray;

    private PreferenceSettings _appPrefs;

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    HashMap<Integer,String> mFragmentTags = new HashMap<Integer,String>();

    SQLhandler sqlHandler;
    String SQL_Filename;
    int SQLDBSize;
    String Category;
    String PortfolioCategory;
    String PortfolioSubCategory;

    float Price;
    int Shares;
    float GainLoss;
    float GainLossPerc;
    String ChangePerc;
    float Basis;
    int Comission;

    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> laptopCollection;
    ExpandableListView expListView;

    Map<String, String> SubcategoryMap = new HashMap<>();

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] navigation;

    static final int SQL_POPUP_WIDTH = 1000;
    static final int SQL_POPUP_HEIGHT = 1500;
    static final int SQL_POPUP_GRAVITY_X = 0;
    static final int SQL_POPUP_GRAVITY_Y = 0;

    private PopupWindow popupWindow;

    static final int REQUEST_INFO = 1000;
    static final int REQUEST_YAHOO = 2000;

    //http://www.nasdaq.com/symbol/aapl/option-chain
    //**********************************************************************************************
    //***   onCreate
    //**********************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.portfolio_view_pager);

        SubcategoryMap.put("INDEX ETF","DOW,NASDAQ,S&P,MISC");
        SubcategoryMap.put("REGION ETF","ASIA,EUROPE,LATIN AMERICA,MISC");
        SubcategoryMap.put("COMMODITY ETF","FOSSIL,METAL,AGR,MISC");
        SubcategoryMap.put("TECH","MISC");
        SubcategoryMap.put("FINANCE","BANK,WALL,PEER,MISC");
        SubcategoryMap.put("MUTUAL","GEO,COM,MISC");

//        System.out.println(map.get(1)); // prints Foo

        //------------------------------------------------------------------------------------------
        //---   Get Card Details
        //------------------------------------------------------------------------------------------

        Bundle extras = getIntent().getExtras();
        Category = extras.getString("PORTFOLIO_CATEGORY");

        CategoryArray = getResources().getStringArray(R.array.categories);

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
        //---   ViewPager
        //------------------------------------------------------------------------------------------

        mPager = (ViewPager) findViewById(pager);
        mPagerAdapter = new CardViewPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPagerAdapter.notifyDataSetChanged();

        //------------------------------------------------------------------------------------------
        //---   SQLite Setup
        //------------------------------------------------------------------------------------------

        sqlHandler = new SQLhandler(PortfolioView.this,_appPrefs.getSQLStockDBName(),
                Integer.parseInt(_appPrefs.getSQLStockDBVersion()));

        //----------------------------------------------------------------------------------
        //---   Navigation Drawer
        //----------------------------------------------------------------------------------

        navigation = getResources().getStringArray(R.array.navigation);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,navigation));
        mDrawerList.setOnItemClickListener(new Navigator(this));

        //------------------------------------------------------------------------------------------
        //---   ViewPager Listener
        //------------------------------------------------------------------------------------------

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // triggered many many times when touching and trying to scroll it, once released it finishes with new position
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            //--------------------------------------------------------------------------------------
            //---   Page Slide
            //--------------------------------------------------------------------------------------
            @Override
            public void onPageSelected(int position) {

//                if (!ButtonPush) {
//                    if (lastPage > position) {
//                        index--;
//                    } else {
//                        index++;
//                    }
//                }
//                lastPage = position;
//                ButtonPush = false;
//
//                Log.i("LOG: (CV) PAGEVIEWER", "onPageSelected - new page: " + position);
//
                Fragment fragment = ((CardViewPagerAdapter) mPager.getAdapter()).getFragment(position);

                View Fragmentview = fragment.getView();

                refreshView(Fragmentview);

                Category = CategoryArray[position];

                refreshCard();

                TextView txtCategory = (TextView) findViewById(R.id.txtCategory);
                txtCategory.setText(Category + " (" + SQLDBSize + ")");
            }

            //SCROLL_STATE_IDLE=0, SCROLL_STATE_DRAGGING=1, SCROLL_STATE_SETTLING=2
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //------------------------------------------------------------------------------------------
        //---   Show initial card
        //------------------------------------------------------------------------------------------

        mPager.post(new Runnable() {

            public void run() {

                createGroupList();
                createCollection();

                expListView = (ExpandableListView) findViewById(R.id.laptop_list);

                expListAdapter = new ExpandableListAdapter(PortfolioView.this, groupList, laptopCollection);
                expListView.setAdapter(expListAdapter);

                //------------------------------------------------------------------------------------------
                //---   Layout
                //------------------------------------------------------------------------------------------

                btnPortfolioAdd = (Button) findViewById(R.id.btnPortfolioAdd);
                btnPortfolioAdd.setOnClickListener(PortfolioView.this);

                btnDatabaseSave = (Button) findViewById(R.id.btnDatabaseSave);
                btnDatabaseSave.setOnClickListener(PortfolioView.this);

                btnPortfolioFilterShares = (Button) findViewById(R.id.btnPortfolioFilterShares);
                btnPortfolioFilterShares.setOnClickListener(PortfolioView.this);

                btnPortfolioFilterPerc = (Button) findViewById(R.id.btnPortfolioFilterPerc);
                btnPortfolioFilterPerc.setOnClickListener(PortfolioView.this);

//                refreshCard(currentIndex);

                allStocks = sqlHandler.getStocksByCategory(Category);

//                adapterStocks = new CustomDatabaseAdapter();
//                listViewAllStocks.setAdapter(adapterStocks);

                SQLDBSize = allStocks.size();

                TextView txtCategory = (TextView) findViewById(R.id.txtCategory);
                txtCategory.setText(Category+" ("+SQLDBSize+")");


                Intent intentYahoo = new Intent(PortfolioView.this, EtradeAPI.class);
                ArrayList<String> TickerList = grabTickers();
                intentYahoo.putStringArrayListExtra("TICKER_INDEX_ARRAY", TickerList);
                startActivityForResult(intentYahoo,REQUEST_YAHOO);

            }
        });
    }

    //------------------------------------------------------------------------------------------
    //---   Create list of groups names
    //------------------------------------------------------------------------------------------
    private void createGroupList() {

        allStocks = sqlHandler.getStocksByCategory(Category);

        groupList = new ArrayList<String>();

        SubCategoryArray = SubcategoryMap.get(Category).split(",");

//        groupList.add("HP");
//        groupList.add("Dell");
//        groupList.add("Lenovo");
//        groupList.add("Sony");
//        groupList.add("HCL");
//        groupList.add("Samsung");

        for (String c : SubCategoryArray) {
            groupList.add(c);
        }
    }

    //------------------------------------------------------------------------------------------
    //---   Create collection of subgroups
    //------------------------------------------------------------------------------------------
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

            allStocks = sqlHandler.getStocksBySubcategory(laptop);

            String[] tmpList = new String[allStocks.size()];

            for (int i = 0;i < allStocks.size();i++) {

                Stock tmpStock = allStocks.get(i);

                tmpList[i] = tmpStock.getTicker();

//                if (laptop.equals("HP")) {
//                    loadChild(hpModels);
//                } else if (laptop.equals("Dell"))
//                    loadChild(dellModels);
//                else if (laptop.equals("Sony"))
//                    loadChild(sonyModels);
//                else if (laptop.equals("HCL"))
//                    loadChild(hclModels);
//                else if (laptop.equals("Samsung"))
//                    loadChild(samsungModels);
//                else
//                    loadChild(lenovoModels);
            }

            loadChild(tmpList);

            laptopCollection.put(laptop, childList);
        }
    }

    private void loadChild(String[] laptopModels) {
        childList = new ArrayList<String>();
        for (String model : laptopModels)
            childList.add(model);
    }

//    private void setGroupIndicatorToRight() {
//        /* Get the screen width */
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int width = dm.widthPixels;
//
//        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width
//                - getDipsFromPixel(5));
//    }

//    // Convert pixel to dip
//    public int getDipsFromPixel(float pixels) {
//        // Get the screen's density scale
//        final float scale = getResources().getDisplayMetrics().density;
//        // Convert the dps to pixels, based on density scale
//        return (int) (pixels * scale + 0.5f);
//    }

    //**********************************************************************************************
    //***   Custom Expandable Adapter for PortfolioPage
    //**********************************************************************************************

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Activity context;
        private Map<String, List<String>> laptopCollections;
        private List<String> laptops;

        private int lastExpandedGroupPosition = -1;

        //------------------------------------------------------------------------------------------
        //---   Constructor
        //------------------------------------------------------------------------------------------
        public ExpandableListAdapter(Activity context, List<String> laptops,Map<String, List<String>> laptopCollections) {

            this.context = context;
            this.laptopCollections = laptopCollections;
            this.laptops = laptops;
        }

        //------------------------------------------------------------------------------------------
        //---   Collapse last group, when new group is expanded
        //------------------------------------------------------------------------------------------
//        @Override
//        public void onGroupExpanded(int groupPosition){
//            //collapse the old expanded group, if not the same
//            //as new group to expand
//            if(groupPosition != lastExpandedGroupPosition){
//                expListView.collapseGroup(lastExpandedGroupPosition);
//            }
//
//            super.onGroupExpanded(groupPosition);
//            lastExpandedGroupPosition = groupPosition;
//        }

        //------------------------------------------------------------------------------------------
        //---   Update quotes, when new group is expanded
        //------------------------------------------------------------------------------------------
        @Override
        public void onGroupExpanded(int groupPosition){

//            Intent intentYahoo = new Intent(PortfolioView.this, EtradeAPI.class);
//            ArrayList<String> TickerList = grabTickers();
//            intentYahoo.putStringArrayListExtra("TICKER_INDEX_ARRAY", TickerList);
//            startActivityForResult(intentYahoo,REQUEST_YAHOO);

            super.onGroupExpanded(groupPosition);
            lastExpandedGroupPosition = groupPosition;
        }

        //------------------------------------------------------------------------------------------
        //---   Constructor
        //------------------------------------------------------------------------------------------
        public Object getChild(int groupPosition, int childPosition) {
            return laptopCollections.get(laptops.get(groupPosition)).get(childPosition);
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        //------------------------------------------------------------------------------------------
        //---   Child View (stock Info)
        //------------------------------------------------------------------------------------------
        public View getChildView(final int groupPosition, final int childPosition,boolean isLastChild,
                                 View convertView, ViewGroup parent) {

            final String Ticker = (String) getChild(groupPosition, childPosition);

            LayoutInflater inflater = context.getLayoutInflater();

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.child_item, null);
            }

            RelativeLayout rlhDataBase = (RelativeLayout) convertView.findViewById(R.id.child_item_view);
            rlhDataBase.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.gradient_light_grey_bg, null));

            TextView itemView = (TextView) convertView.findViewById(R.id.portfolioListViewTicker);

            itemView.setText(Ticker);






            String SubCategory = laptops.get(groupPosition);

            allStocks = sqlHandler.getStocksBySubcategory(SubCategory);

            Stock currentStock = allStocks.get(childPosition);

            DecimalFormat df1 = new DecimalFormat("#.#");
            DecimalFormat df2 = new DecimalFormat("#.##");

            Price = currentStock.getPrice();
            Shares = currentStock.getShares();
            ChangePerc = currentStock.getChangePerc();
            Basis = currentStock.getBasis();
            Comission = currentStock.getCommission();

            //--------------------------------------------------------------------------------------
            //---   Ticker
            //------------------------------------------------------------------------------------------
            TextView menuOption = (TextView) convertView.findViewById(R.id.portfolioListViewTicker);
            menuOption.setText(currentStock.getTicker());
            //--------------------------------------------------------------------------------------
            //---   Price
            //------------------------------------------------------------------------------------------
            menuOption = (TextView) convertView.findViewById(R.id.portfolioListViewPrice);
            String PriceFormat = String.format("%.02f", currentStock.getPrice());
            menuOption.setText(PriceFormat);
            //--------------------------------------------------------------------------------------
            //---   Change (Percentage)
            //------------------------------------------------------------------------------------------
            portfolioListViewChangePerc = (TextView) convertView.findViewById(R.id.portfolioListViewChangePerc);
            if (currentStock.getChangePerc().contains("+")) {
                portfolioListViewChangePerc.setTextColor(ContextCompat.getColor(PortfolioView.this, R.color.colorGreenStrong));
            } else {
                portfolioListViewChangePerc.setTextColor(ContextCompat.getColor(PortfolioView.this, R.color.colorRedStrong));
            }
            ChangePerc = ChangePerc.replace("%", "");
            if (ChangePerc.equals("")) {
//                portfolioListViewChangePerc.setText(df1.format(Float.parseFloat(ChangePerc)) + "%");
            } else {
                String ChangePercFormat = String.format("%.02f", Float.parseFloat(ChangePerc));
                portfolioListViewChangePerc.setText(ChangePercFormat + "%");
            }
            //--------------------------------------------------------------------------------------
            //---   Gain/Loss
            //--------------------------------------------------------------------------------------
            menuOption = (TextView) convertView.findViewById(R.id.portfolioListViewGainLoss);
            GainLoss = ((Price - Basis) * Shares) - Comission;
            if (GainLoss >= 0) {
                menuOption.setTextColor(ContextCompat.getColor(PortfolioView.this, R.color.colorGreenStrong));
            } else {
                menuOption.setTextColor(ContextCompat.getColor(PortfolioView.this, R.color.colorRedStrong));
            }
            String GainLossFormat = String.format("%.02f", GainLoss);
            if (Shares < 1) {
                menuOption.setText("----");
            } else {
                menuOption.setText(GainLossFormat);
            }
            //------------------------------------------------------------------------------------------
            menuOption = (TextView) convertView.findViewById(R.id.portfolioListViewGainLossPerc);
            if (Basis == 0) {
                menuOption.setText("---");
            } else {
                if (Shares < 1) {
                    GainLossPerc = ((Price - Basis) * 100) / Basis;
                } else {
                    GainLossPerc = (GainLoss * 100) / (Basis * Shares);
                }
                if (GainLossPerc >= 0) {
                    menuOption.setTextColor(ContextCompat.getColor(PortfolioView.this, R.color.colorGreenStrong));
                } else {
                    menuOption.setTextColor(ContextCompat.getColor(PortfolioView.this, R.color.colorRedStrong));
                }
                String GainLossPercFormat = String.format("%.02f", GainLossPerc);
                menuOption.setText(GainLossPercFormat + "%");
            }

            return convertView;
        }

        public int getChildrenCount(int groupPosition) {
            return laptopCollections.get(laptops.get(groupPosition)).size();
        }

        public Object getGroup(int groupPosition) {
            return laptops.get(groupPosition);
        }

        public int getGroupCount() {
            return laptops.size();
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        //------------------------------------------------------------------------------------------
        //---   Group View (stock Info)
        //------------------------------------------------------------------------------------------
        public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {

            String laptopName = (String) getGroup(groupPosition);

            if (convertView == null) {

                LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.group_item,null);
            }

            RelativeLayout rlhDataBase = (RelativeLayout) convertView.findViewById(R.id.group_item_view);
            rlhDataBase.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.gradient_dark_grey_bg, null));

            TextView item = (TextView) convertView.findViewById(R.id.portfolioListViewGroupName);

//                item.setTypeface(null, Typeface.BOLD);

            item.setText(laptopName);

            return convertView;
        }

        public boolean hasStableIds() {
            return true;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    //**********************************************************************************************
    //***   Page Adapter (FragmentStatePagerAdapter caused weirdness in HashMap)
    //**********************************************************************************************
    private class CardViewPagerAdapter extends FragmentPagerAdapter {

//        HashMap<Integer,String> mFragmentTags = new HashMap<Integer,String>();

        public CardViewPagerAdapter(FragmentManager fm) {

            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return new PortfolioViewFragment();
        }

        @Override
        public int getCount() {

            return getResources().getStringArray(R.array.categories).length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            Object obj = super.instantiateItem(container, position);

            if (obj instanceof Fragment) {

                Fragment f = (Fragment) obj;
                String tag = f.getTag();
                mFragmentTags.put(position, tag);
            }

            return obj;
        }

        public Fragment getFragment(int position) {

            String tag = mFragmentTags.get(position);

            if (tag == null)
                return null;

            return getSupportFragmentManager().findFragmentByTag(tag);
        }
    }

    //**********************************************************************************************
    //***   Control of bottom set of Buttons
    //**********************************************************************************************
    @Override
    public void onClick(View view) {

        int position, heap;

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
//                popupWindow.update();

                popupWindow.showAtLocation(findViewById(R.id.rlv_portfolio_view_pager),
                        Gravity.CENTER, SQL_POPUP_GRAVITY_X, SQL_POPUP_GRAVITY_Y);

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
                //---   Soft keyboard popping up
                //----------------------------------------------------------------------------------

                edtPortfolioTicker.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edtPortfolioTicker, InputMethodManager.SHOW_IMPLICIT);

                //----------------------------------------------------------------------------------
                //---   Category Spinner
                //----------------------------------------------------------------------------------
                Portfolio_Spinner = (Spinner) popupView.findViewById(R.id.spnPortfolioCategory);
                Portfolio_Spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.categories, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                Portfolio_Spinner.setAdapter(adapter);
                Portfolio_Spinner.setSelection(Arrays.asList(CategoryArray).indexOf(Category));

                //----------------------------------------------------------------------------------
                //---   Subcategory Spinner
                //----------------------------------------------------------------------------------
                Portfolio_SubCategory_Spinner = (Spinner) popupView.findViewById(R.id.spnPortfolioSubCategory);
                Portfolio_SubCategory_Spinner.setOnItemSelectedListener(new PortfolioSubCategoryOnItemSelectedListener());

                Portfolio_SubCategory_Adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, SubCategoryArray);
                Portfolio_SubCategory_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Portfolio_SubCategory_Spinner.setAdapter(Portfolio_SubCategory_Adapter);
//                Portfolio_SubCategory_Spinner.setSelection(Arrays.asList(CategoryArray).indexOf(Category));

                //----------------------------------------------------------------------------------
                //---
                //----------------------------------------------------------------------------------
                btnPortfolioAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Stock newStock = new Stock();

                        newStock.setTicker(edtPortfolioTicker.getText().toString());
                        newStock.setCategory(PortfolioCategory);
                        newStock.setSubcategory(PortfolioSubCategory);
                        newStock.setShares(Integer.parseInt(edtPortfolioShares.getText().toString()));
                        newStock.setBasis(Float.parseFloat(edtPortfolioBasis.getText().toString()));
                        newStock.setCommission(Integer.parseInt(edtPortfolioComission.getText().toString()));

                        sqlHandler.addStock(newStock);

                        popupWindow.dismiss();
                    }
                });

                break;

            //--------------------------------------------------------------------------------------
            //---   Filter non-owned Stocks
            //--------------------------------------------------------------------------------------
            case R.id.btnPortfolioFilterShares:

                position = 0;

                ArrayList<Integer> FilterShares = new ArrayList<Integer>();

                for (Stock stock : allStocks) {

                    //------------------------------------------------------------------------------
                    //---   Currently owning shares ...
                    //------------------------------------------------------------------------------
                    if (stock.getShares() > 0) {

//                        Log.i("LOG: (PF) SHARES", "Shares: " + stock.getTicker());

                    //------------------------------------------------------------------------------
                    //---   ... not owning shares ...
                    //------------------------------------------------------------------------------
                    } else {

                        FilterShares.add(position);
                    }

                    position++;
                }

                Log.i("LOG: (PF) SHARES", "Count: " + adapterStocks.getCount());

                //------------------------------------------------------------------------------
                //---   Remove filtered ones from ListView
                //------------------------------------------------------------------------------
                heap = 0;

                for (int i : FilterShares) {

                    Stock toRemove = adapterStocks.getItem(i-heap);
                    adapterStocks.remove(toRemove);
                    heap++;
                }

                break;

            //--------------------------------------------------------------------------------------
            //---   Filter < 1% Gain/Loss
            //--------------------------------------------------------------------------------------
            case R.id.btnPortfolioFilterPerc:

                position = 0;

                ArrayList<Integer> FilterPerc = new ArrayList<Integer>();

                for (Stock stock : allStocks) {

                    if (abs(Float.parseFloat(stock.getChangePerc().replace("%",""))) > 1) {

                        Log.i("LOG: (PF) PERC", "Perc: " + stock.getTicker());

                    } else {

                        FilterPerc.add(position);
                    }
                    position++;
                }

                Log.i("LOG: (PF) PERC", "Count: " + adapterStocks.getCount());

                heap = 0;

                for (int i : FilterPerc) {

                    Log.i("LOG: (PF) PERC", "Perc: " + i + (i-heap));

                    Stock toRemove = adapterStocks.getItem(i-heap);
                    adapterStocks.remove(toRemove);
                    heap++;
                }

                break;
        }
    }

    //**********************************************************************************************
    //***   CustomOnItemSelectedListener (for Spinner)
    //**********************************************************************************************
    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {

            PortfolioCategory = parent.getItemAtPosition(pos).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }

    public class PortfolioSubCategoryOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {

            PortfolioSubCategory = parent.getItemAtPosition(pos).toString();
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
        menu.add(0, v.getId(), 0, "Clear");
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
                startActivityForResult(intent2Info,REQUEST_INFO);

                break;

            //--------------------------------------------------------------------------------------
            //---   CLEAR (Copy Price to Base)
            //--------------------------------------------------------------------------------------
            case "Clear":

                currentStock.setShares(0);
                currentStock.setCommission(0);

                sqlHandler.updateStock(currentStock);

                adapterStocks.notifyDataSetChanged();

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

        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),
                R.drawable.gradient_dark_grey_bg, null));

        MenuItem menu_spinner = menu.findItem(R.id.menu_spinner);
        menu_spinner.setVisible(false);

        MenuItem menu_edit = menu.findItem(R.id.menu_edit);
        menu_edit.setVisible(false);

        String SQLFileName = "0";

        //------------------------------------------------------------------------------------------
        //---   Show active Category
        //------------------------------------------------------------------------------------------

        TextView txtCategory = (TextView) findViewById(R.id.txtCategory);
        txtCategory.setText(Category + " (" + SQLDBSize + ")");
        txtCategory.setTextColor(ContextCompat.getColor(this, R.color.colorGrey3));

        //------------------------------------------------------------------------------------------
        //---   Touch header -> Category
        //------------------------------------------------------------------------------------------
        txtCategory.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {

                switch (arg1.getAction()) {

                    case MotionEvent.ACTION_DOWN: {

                        Intent intentTraining = new Intent(PortfolioView.this, CategoryView.class);
                        startActivity(intentTraining);

                        break;
                    }
                }

                return true;
            }
        });

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

            Intent intentYahoo = new Intent(PortfolioView.this, EtradeAPI.class);
            ArrayList<String> TickerList = grabTickers();
            intentYahoo.putStringArrayListExtra("TICKER_INDEX_ARRAY", TickerList);
            startActivityForResult(intentYahoo,REQUEST_YAHOO);
        }

        return super.onOptionsItemSelected(item);
    }

    //**********************************************************************************************
    //***   Method: Collect all Ticker for single API call
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
    //***   onActivityResult (returning from API call)
    //**********************************************************************************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //--------------------------------------------------------------------------------------
        //---   Return via finish()
        //--------------------------------------------------------------------------------------
        if (resultCode == Activity.RESULT_OK) {

            //------------------------------------------------------------------------------------------
            //---   Return from YAHOO
            //------------------------------------------------------------------------------------------
            if (requestCode == REQUEST_INFO) {

                expListAdapter = new ExpandableListAdapter(PortfolioView.this, groupList, laptopCollection);
                expListView.setAdapter(expListAdapter);
                expListAdapter.notifyDataSetChanged();
            }

            //------------------------------------------------------------------------------------------
            //---   Return from YAHOO
            //------------------------------------------------------------------------------------------
            if (requestCode == REQUEST_YAHOO) {

                expListAdapter = new ExpandableListAdapter(PortfolioView.this, groupList, laptopCollection);
                expListView.setAdapter(expListAdapter);
                expListAdapter.notifyDataSetChanged();
            }
        }
    }

    //**********************************************************************************************
    //***   Method: Refresh Card Layout
    //**********************************************************************************************
    private void refreshView(View view) {

        //------------------------------------------------------------------------------------------
        //---   Layout
        //------------------------------------------------------------------------------------------

//        listViewAllStocks = (ListView) view.findViewById(listViewAllStocks);
    }

    //**********************************************************************************************
    //***   Method: Refresh Card Layout
    //**********************************************************************************************
    private void refreshCard () {

        allStocks = sqlHandler.getStocksByCategory(Category);

        SQLDBSize = allStocks.size();

        TextView txtCategory = (TextView) findViewById(R.id.txtCategory);
        txtCategory.setText(Category+" ("+SQLDBSize+")");

        expListAdapter = new ExpandableListAdapter(PortfolioView.this, groupList, laptopCollection);
        expListView.setAdapter(expListAdapter);
        expListAdapter.notifyDataSetChanged();
    }
}