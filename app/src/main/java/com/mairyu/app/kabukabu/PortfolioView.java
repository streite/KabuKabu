package com.mairyu.app.kabukabu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.util.Log.i;
import static com.mairyu.app.kabukabu.R.id.menu_refresh;
import static com.mairyu.app.kabukabu.R.id.pager;
import static java.lang.StrictMath.abs;

//==================================================================================================
//===   PortfolioView
//==================================================================================================
public class PortfolioView extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Stock> allStocks = new ArrayList<>();
    private ArrayList<Stock> allSubStocks = new ArrayList<>();
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
    private String[] SubcategoryArray;

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

    ArrayList<String> TickerList;

    float Price;
    int Shares;
    float GainLoss;
    float GainLossPerc;
    String ChangePerc;
    float Basis;
    int Comission;
    String Leverage;

    List<String> SubcategoryList;
    List<String> childList;
    Map<String, List<String>> subgroupCollection;
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

    DateFormat df = new SimpleDateFormat("MM-dd-yyyy");

    static final int REQUEST_INFO = 1000;
    static final int REQUEST_YAHOO = 2000;
    static final int REQUEST_YAHOO2 = 2001;
    static final int REQUEST_YAHOO3 = 2002;
    static final int REQUEST_YAHOO4 = 2003;

    //http://www.nasdaq.com/symbol/aapl/option-chain
//    https://finance.yahoo.com/calendar/earnings?&day=2017-11-14
//    https://eresearch.fidelity.com/eresearch/conferenceCalls.jhtml?tab=earnings&begindate=11/14/2017
//    http://www.nasdaq.com/earnings/earnings-calendar.aspx?date=2017-Nov-14

    //**********************************************************************************************
    //***   onCreate
    //**********************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.portfolio_view_pager);

        SubcategoryMap.put("INDEX ETF","DOW,NASDAQ,S&P,RUSSEL,MISC");
        SubcategoryMap.put("REGION ETF","ASIA,EUROPE,LATIN AMERICA,MISC");
        SubcategoryMap.put("COMMODITY ETF","METAL,AGRICULTURE,ENERGY,MISC");
        SubcategoryMap.put("OTHER ETF","REAL ESTATE,TECH,BIOTECH,FINANCE");
        SubcategoryMap.put("TECH","SEMICONDUCTOR,WWW,ELECTRONICS,MISC");
        SubcategoryMap.put("FINANCE","WALL STREET,BANK,PEER,MISC");
        SubcategoryMap.put("CONSUME","COMMUNICATION,MERCHANDISE,MEDIA,ENTERTAINMENT,FOOD,MISC");
        SubcategoryMap.put("DEFENSE","MISC");
        SubcategoryMap.put("HEALTH","DRUGS,INSURANCE,MISC");
        SubcategoryMap.put("TRANSPORTATION","CAR,AIRLINE,MISC");
        SubcategoryMap.put("MISC","MISC");
        SubcategoryMap.put("MUTUAL","GEO,COM,MISC");

        //------------------------------------------------------------------------------------------
        //---   Get Card Details
        //------------------------------------------------------------------------------------------

        Bundle extras = getIntent().getExtras();
        Category = extras.getString("PORTFOLIO_CATEGORY");

        //------------------------------------------------------------------------------------------
        //---   from 'strings.xml'
        //------------------------------------------------------------------------------------------

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

                //----------------------------------------------------------------------------------
                //---   Update Category Header
                //----------------------------------------------------------------------------------

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

                createSubcategoryList();
                createSubgroupList();

                expListView = (ExpandableListView) findViewById(R.id.StockExpandList);

                expListAdapter = new ExpandableListAdapter(PortfolioView.this, SubcategoryList, subgroupCollection);
                expListView.setAdapter(expListAdapter);

                registerForContextMenu(expListView);

//                expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//
//                    public boolean onChildClick(ExpandableListView parent, View v,
//                                                int groupPosition, int childPosition, long id) {
//
//                        final String selected = (String) expListAdapter.getChild(groupPosition, childPosition);
//
//                        Toast.makeText(getBaseContext(), selected, Toast.LENGTH_SHORT).show();
//
//                        return true;
//                    }
//                });

//                expListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                    @Override
//                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                        //Message
//                        Toast.makeText(getBaseContext(), position+"", Toast.LENGTH_SHORT).show();
//
//                        return true;
//                    }
//                });

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

                Intent intentYahoo = new Intent(PortfolioView.this, NasdaqAPI.class);
                TickerList = grabTickers();
                ArrayList<String> TickerPartList = new ArrayList<String>(TickerList.subList(0, TickerList.size()));
                intentYahoo.putStringArrayListExtra("TICKER_INDEX_ARRAY", TickerPartList);
                startActivityForResult(intentYahoo,REQUEST_YAHOO);
            }
        });
    }

    //------------------------------------------------------------------------------------------
    //---   Create list of all Subcategories in this group
    //------------------------------------------------------------------------------------------
    private void createSubcategoryList() {

        SubcategoryList = new ArrayList<String>();

        SubcategoryArray = SubcategoryMap.get(Category).split(",");

        for (String SubCategory : SubcategoryArray) {

            SubcategoryList.add(SubCategory);
        }
    }

    //----------------------------------------------------------------------------------------------
    //---   Create collection of subgroups
    //----------------------------------------------------------------------------------------------
    private void createSubgroupList() {

        subgroupCollection = new LinkedHashMap<String, List<String>>();

        //----------------------------------------------------------------------------------------------
        //---   Create Child List for each Subcategory
        //----------------------------------------------------------------------------------------------

        for (String subCategory : SubcategoryList) {

            allSubStocks = sqlHandler.getStocksBySubcategory(Category,subCategory);

            String[] tmpList = new String[allSubStocks.size()];

            for (int i = 0; i < allSubStocks.size(); i++) {

                Stock tmpStock = allSubStocks.get(i);

                tmpList[i] = tmpStock.getTicker();
            }

//            loadChild(tmpList);

            childList = new ArrayList<String>();

            for (String model : tmpList)
                childList.add(model);

            subgroupCollection.put(subCategory, childList);
        }
    }

//    private void loadChild(String[] laptopModels) {
//
//        childList = new ArrayList<String>();
//        for (String model : laptopModels)
//            childList.add(model);
//    }

    //**********************************************************************************************
    //***   Custom Expandable Adapter for PortfolioPage
    //**********************************************************************************************

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Activity context;
        private Map<String, List<String>> SubgroupNames;
        private List<String> GroupNames;

        private int lastExpandedGroupPosition = -1;

        //------------------------------------------------------------------------------------------
        //---   Constructor
        //------------------------------------------------------------------------------------------
        public ExpandableListAdapter(Activity context, List<String> GroupNames,
                                     Map<String, List<String>> SubgroupNames) {

            this.context = context;
            this.SubgroupNames = SubgroupNames;
            this.GroupNames = GroupNames;
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
        //---   Group is expanded
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
        //---   Group is collapsed
        //------------------------------------------------------------------------------------------
//        @Override
//        public void onGroupCollapse(int groupPosition){
//
//            super.onGroupCollapsed(groupPosition);
//        }

        //------------------------------------------------------------------------------------------
        //---
        //------------------------------------------------------------------------------------------
        public Object getChild(int groupPosition, int childPosition) {

            return SubgroupNames.get(GroupNames.get(groupPosition)).get(childPosition);
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        //------------------------------------------------------------------------------------------
        //---   Child View (stock Info)
        //------------------------------------------------------------------------------------------
        public View getChildView(final int groupPosition, final int childPosition,boolean isLastChild,
                                 View childView, ViewGroup parent) {

            LayoutInflater inflater = context.getLayoutInflater();

            if (childView == null) {
                childView = inflater.inflate(R.layout.child_item, null);
            }

            RelativeLayout rlhDataBase = (RelativeLayout) childView.findViewById(R.id.child_item_view);
            rlhDataBase.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.gradient_light_grey_bg, null));

            final String Ticker = (String) getChild(groupPosition, childPosition);

            TextView itemView = (TextView) childView.findViewById(R.id.portfolioListViewTicker);

            itemView.setText(Ticker);

            String SubCategory = GroupNames.get(groupPosition);

            allSubStocks = sqlHandler.getStocksBySubcategory(Category,SubCategory);

            Stock currentStock = allSubStocks.get(childPosition);

            DecimalFormat df1 = new DecimalFormat("#.#");
            DecimalFormat df2 = new DecimalFormat("#.##");

            Price = currentStock.getPrice();
            Shares = currentStock.getShares();
            ChangePerc = currentStock.getChangePerc();
            Basis = currentStock.getBasis();
            Comission = currentStock.getCommission();
            Leverage = currentStock.getLeverage();

            //--------------------------------------------------------------------------------------
            //---   Leverage Indicator
            //------------------------------------------------------------------------------------------
            ImageView childLeverage = (ImageView) childView.findViewById(R.id.portfolioListViewLeverage);
            ImageView childWatch = (ImageView) childView.findViewById(R.id.portfolioListViewWatch);
            childWatch.setVisibility(View.INVISIBLE);
            childLeverage.setVisibility(View.VISIBLE);

            switch (Leverage) {

                case "3": childLeverage.setImageResource(R.mipmap.ic_3_green); break;
                case "2": childLeverage.setImageResource(R.mipmap.ic_2_green); break;
                case "1": childLeverage.setImageResource(R.mipmap.ic_1_green); break;
                case "0": childLeverage.setVisibility(View.INVISIBLE); break;
                case "-1": childLeverage.setImageResource(R.mipmap.ic_1_red); break;
                case "-2": childLeverage.setImageResource(R.mipmap.ic_2_red); break;
                case "-3": childLeverage.setImageResource(R.mipmap.ic_3_red); break;
            }

            //--------------------------------------------------------------------------------------
            //---   Ticker
            //------------------------------------------------------------------------------------------
            TextView portfolioListViewTicker = (TextView) childView.findViewById(R.id.portfolioListViewTicker);
            portfolioListViewTicker.setText(currentStock.getTicker());

            portfolioListViewTicker.setTextColor(ContextCompat.getColor(PortfolioView.this, R.color.colorGrey1));
            portfolioListViewTicker.setTypeface(null, Typeface.NORMAL);

            //--------------------------------------------------------------------------------------
            //---   Company Name
            //------------------------------------------------------------------------------------------
            TextView portfolioListViewName = (TextView) childView.findViewById(R.id.portfolioListViewName);
            portfolioListViewName.setText(currentStock.getCompany());

            portfolioListViewName.setTextColor(ContextCompat.getColor(PortfolioView.this, R.color.colorGrey1));
            portfolioListViewName.setTypeface(null, Typeface.NORMAL);

            //--------------------------------------------------------------------------------------
            //---   Price
            //------------------------------------------------------------------------------------------
            TextView portfolioListViewPrice = (TextView) childView.findViewById(R.id.portfolioListViewPrice);
            String PriceFormat = String.format("%.02f", currentStock.getPrice());
            portfolioListViewPrice.setText(PriceFormat);

            //--------------------------------------------------------------------------------------
            //---   Change (Percentage)
            //------------------------------------------------------------------------------------------
            portfolioListViewChangePerc = (TextView) childView.findViewById(R.id.portfolioListViewChangePerc);
            if (currentStock.getChangePerc().contains("+")) {
                portfolioListViewChangePerc.setTextColor(ContextCompat.getColor(PortfolioView.this, R.color.colorGreenStrong));
            } else {
                portfolioListViewChangePerc.setTextColor(ContextCompat.getColor(PortfolioView.this, R.color.colorRedStrong));
            }
            ChangePerc = ChangePerc.replace("%", "");
            ChangePerc = ChangePerc.replace("--", "-"); // not sure why we sometimes get '--'
            if (ChangePerc.equals("")) {
//                portfolioListViewChangePerc.setText(df1.format(Float.parseFloat(ChangePerc)) + "%");
            } else {
                String ChangePercFormat = String.format("%.02f", Float.parseFloat(ChangePerc));
                portfolioListViewChangePerc.setText(ChangePercFormat + "%");

                ChangePerc = ChangePerc.replace("+", "");

                if (Float.parseFloat(ChangePerc) > 1) {
                    rlhDataBase.setBackground(ResourcesCompat.getDrawable(getResources(), R.color.colorGreyGreen, null));
                }
                if (Float.parseFloat(ChangePerc) < -1) {
                    rlhDataBase.setBackground(ResourcesCompat.getDrawable(getResources(), R.color.colorGreyRed, null));
                }
            }

            //--------------------------------------------------------------------------------------
            //---   Gain/Loss ($)
            //--------------------------------------------------------------------------------------
            TextView menuOption = (TextView) childView.findViewById(R.id.portfolioListViewGainLoss);
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
            //--------------------------------------------------------------------------------------
            //---   Gain/Loss (%)
            //--------------------------------------------------------------------------------------
            TextView portfolioListViewGainLossPerc = (TextView) childView.findViewById(R.id.portfolioListViewGainLossPerc);

            if (Basis == 0) {

                portfolioListViewGainLossPerc.setText("---");

            } else {

                if (Shares < 1) {
                    GainLossPerc = ((Price - Basis) * 100) / Basis;
                } else {
                    GainLossPerc = (GainLoss * 100) / (Basis * Shares);
                }
                if (GainLossPerc >= 0) {
                    portfolioListViewGainLossPerc.setTextColor(ContextCompat.getColor(PortfolioView.this, R.color.colorGreenStrong));
                } else {
                    portfolioListViewGainLossPerc.setTextColor(ContextCompat.getColor(PortfolioView.this, R.color.colorRedStrong));
                }
                String GainLossPercFormat = String.format("%.02f", GainLossPerc);
                portfolioListViewGainLossPerc.setText(GainLossPercFormat + "%");

                if ((GainLossPerc < 0) && (Shares < 1) && (Basis != 0)) {
                    portfolioListViewTicker.setTextColor(ContextCompat.getColor(PortfolioView.this, R.color.colorRedStrong));
                    portfolioListViewTicker.setTypeface(null, Typeface.BOLD);

                    portfolioListViewName.setTextColor(ContextCompat.getColor(PortfolioView.this, R.color.colorRedStrong));
                    portfolioListViewName.setTypeface(null, Typeface.BOLD);
                }

                //--------------------------------------------------------------------------------------
                //---   Wash Sale
                //--------------------------------------------------------------------------------------
                Calendar calendarNow = Calendar.getInstance();
                Date rightNow = calendarNow.getTime();

                try {

                    if (currentStock.getWatch() == 1) {

                        String sleepDateString = currentStock.getDate();
                        Date sleepDateStamp = df.parse(sleepDateString);

                        long diff = Math.round((rightNow.getTime() - sleepDateStamp.getTime()) / (double) 86400000);

                        if (diff < 30) {

                            childWatch.setImageResource(R.mipmap.ic_snooze_blue);
                            childWatch.setVisibility(View.VISIBLE);

                            rlhDataBase.setBackground(ResourcesCompat.getDrawable(getResources(), R.color.colorGreyBlue, null));
                        }
                    }

                } catch (ParseException e) {

                    i("LOG: (TP) CATCH", "Bad Date ");
                }

            }

            //--------------------------------------------------------------------------------------
            //---   Buy/Sell Tag
            //--------------------------------------------------------------------------------------

            if (currentStock.getWatch() == 2) {

                childWatch.setImageResource(R.drawable.ic_thumb_up_brown);
                childWatch.setVisibility(View.VISIBLE);
            }

            return childView;
        }

        //------------------------------------------------------------------------------------------
        //---
        //------------------------------------------------------------------------------------------
        public int getChildrenCount(int groupPosition) {

            return SubgroupNames.get(GroupNames.get(groupPosition)).size();
        }

        public Object getGroup(int groupPosition) {

            return GroupNames.get(groupPosition);
        }

        public int getGroupCount() {

            return GroupNames.size();
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        //------------------------------------------------------------------------------------------
        //---   Group View (stock Info)
        //------------------------------------------------------------------------------------------
        public View getGroupView(int groupPosition, boolean isExpanded, View groupView, ViewGroup parent) {

            String GroupName = (String) getGroup(groupPosition);

            if (groupView == null) {

                LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                groupView = infalInflater.inflate(R.layout.group_item,null);
            }

            RelativeLayout rlhDataBase = (RelativeLayout) groupView.findViewById(R.id.group_item_view);
            rlhDataBase.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.gradient_dark_grey_bg, null));

            TextView GroupHeader = (TextView) groupView.findViewById(R.id.portfolioListViewGroupName);

            //--------------------------------------------------------------------------------------
            //---   Update Category Header
            //--------------------------------------------------------------------------------------

            GroupName = (GroupName + " (" + allSubStocks.size() + ")");

            GroupHeader.setTypeface(null, Typeface.BOLD);

            GroupHeader.setText(GroupName);

            return groupView;
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

        //------------------------------------------------------------------------------------------
        //---   Constructor
        //------------------------------------------------------------------------------------------
        public CardViewPagerAdapter(FragmentManager fm) {

            super(fm);
        }

        //------------------------------------------------------------------------------------------
        //---
        //------------------------------------------------------------------------------------------
        @Override
        public Fragment getItem(int position) {

            return new PortfolioViewFragment();
        }

        //------------------------------------------------------------------------------------------
        //---   How many pages?
        //------------------------------------------------------------------------------------------
        @Override
        public int getCount() {

            return getResources().getStringArray(R.array.categories).length;
        }

        //------------------------------------------------------------------------------------------
        //---
        //------------------------------------------------------------------------------------------
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

        //------------------------------------------------------------------------------------------
        //---
        //------------------------------------------------------------------------------------------
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

                Portfolio_SubCategory_Adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, SubcategoryArray);
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
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;

        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);

        // Show context menu for groups
        if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            menu.setHeaderTitle("Group");
            menu.add(0, 0, 1, "Delete");

            // Show context menu for children
        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {

            menu.setHeaderTitle("Select The Action");

            menu.add(0, v.getId(), 0, "Add");
            menu.add(0, v.getId(), 0, "Edit");
            menu.add(0, v.getId(), 0, "Delete");
            menu.add(0, v.getId(), 0, "Clear");
            menu.add(0, v.getId(), 0, "Tag Buy");
            menu.add(0, v.getId(), 0, "Tag Sell");
            menu.add(0, v.getId(), 0, "Clear Tag");
        }
    }

    //**********************************************************************************************
    //***   onContextItemSelected (Context Menu)
    //**********************************************************************************************
    @Override
    public boolean onContextItemSelected(MenuItem item){

        ExpandableListView.ExpandableListContextMenuInfo info =
                (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();

        CharSequence Title = item.getTitle();

        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);

        i("LOG: (PV) Position", "Group: " + groupPosition + " Child: " + childPosition);

        if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            // do something with parent

        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            // do someting with child

            String Ticker = subgroupCollection.get(SubcategoryList.get(groupPosition)).get(childPosition);

//            Stock currentStock = allStocks.get(childPosition);
            Stock currentStock = sqlHandler.getStocksByTicker(Ticker);

            switch (Title.toString()) {

                //--------------------------------------------------------------------------------------
                //---   EDIT
                //--------------------------------------------------------------------------------------
                case "Edit":

                    Intent intent2Info = new Intent(PortfolioView.this, StockInfo.class);

                    intent2Info.putExtra("SQL_STOCK_ID", currentStock.getId());

                    //---   ==> CardView
                    startActivityForResult(intent2Info, REQUEST_INFO);

                    break;

                //----------------------------------------------------------------------------------
                //---   CLEAR (Copy Price to Base)
                //----------------------------------------------------------------------------------
                case "Clear":

                    currentStock.setShares(0);
                    currentStock.setCommission(0);

                    sqlHandler.updateStock(currentStock);

                    adapterStocks.notifyDataSetChanged();

                    break;

                //----------------------------------------------------------------------------------
                //---   Tag for Buy
                //----------------------------------------------------------------------------------
                case "Tag Buy":

                    currentStock.setWatch(2);

                    sqlHandler.updateStock(currentStock);

//                    adapterStocks.notifyDataSetChanged();

                    break;

                //----------------------------------------------------------------------------------
                //---   Tag for Sell
                //----------------------------------------------------------------------------------
                case "Tag Sell":

                    currentStock.setWatch(3);

                    sqlHandler.updateStock(currentStock);

                    adapterStocks.notifyDataSetChanged();

                    break;

                //----------------------------------------------------------------------------------
                //---   Clear Tag
                //----------------------------------------------------------------------------------
                case "Clear Tag":

                    currentStock.setWatch(0);

                    sqlHandler.updateStock(currentStock);

//                    adapterStocks.notifyDataSetChanged();

                    break;
            }

        }

        return super.onContextItemSelected(item);
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

            Intent intentYahoo = new Intent(PortfolioView.this, NasdaqAPI.class);
            ArrayList<String> TickerList = grabTickers();
            ArrayList<String> TickerPartList = new ArrayList<String>(TickerList.subList(0, TickerList.size()));
            intentYahoo.putStringArrayListExtra("TICKER_INDEX_ARRAY", TickerPartList);
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
            //---   Return from Stock Info
            //------------------------------------------------------------------------------------------
            if (requestCode == REQUEST_INFO) {

                expListAdapter = new ExpandableListAdapter(PortfolioView.this, SubcategoryList, subgroupCollection);
                expListView.setAdapter(expListAdapter);
                expListAdapter.notifyDataSetChanged();
            }

            //------------------------------------------------------------------------------------------
            //---   Return from YAHOO
            //------------------------------------------------------------------------------------------
            if (requestCode == REQUEST_YAHOO) {

                if (TickerList.size() > 25) {

                    Intent intentYahoo = new Intent(PortfolioView.this, NasdaqAPI.class);
                    ArrayList<String> TickerPartList = new ArrayList<String>(TickerList.subList(25, TickerList.size()));
                    intentYahoo.putStringArrayListExtra("TICKER_INDEX_ARRAY", TickerPartList);
                    startActivityForResult(intentYahoo, REQUEST_YAHOO2);

                } else {

                    expListAdapter = new ExpandableListAdapter(PortfolioView.this, SubcategoryList, subgroupCollection);
                    expListView.setAdapter(expListAdapter);
                    expListAdapter.notifyDataSetChanged();

                    expListView.expandGroup(0);
                }
            }

            //------------------------------------------------------------------------------------------
            //---   Return from YAHOO2
            //------------------------------------------------------------------------------------------
            if (requestCode == REQUEST_YAHOO2) {

                if (TickerList.size() > 50) {

                    Intent intentYahoo = new Intent(PortfolioView.this, NasdaqAPI.class);
                    ArrayList<String> TickerPartList = new ArrayList<String>(TickerList.subList(50, TickerList.size()));
                    intentYahoo.putStringArrayListExtra("TICKER_INDEX_ARRAY", TickerPartList);
                    startActivityForResult(intentYahoo, REQUEST_YAHOO3);

                } else {

                    expListAdapter = new ExpandableListAdapter(PortfolioView.this, SubcategoryList, subgroupCollection);
                    expListView.setAdapter(expListAdapter);
                    expListAdapter.notifyDataSetChanged();
                }
            }

            //------------------------------------------------------------------------------------------
            //---   Return from YAHOO3
            //------------------------------------------------------------------------------------------
            if (requestCode == REQUEST_YAHOO3) {

                if (TickerList.size() > 75) {

                    Intent intentYahoo = new Intent(PortfolioView.this, NasdaqAPI.class);
                    ArrayList<String> TickerPartList = new ArrayList<String>(TickerList.subList(75, TickerList.size()));
                    intentYahoo.putStringArrayListExtra("TICKER_INDEX_ARRAY", TickerPartList);
                    startActivityForResult(intentYahoo, REQUEST_YAHOO4);

                } else {

                    expListAdapter = new ExpandableListAdapter(PortfolioView.this, SubcategoryList, subgroupCollection);
                    expListView.setAdapter(expListAdapter);
                    expListAdapter.notifyDataSetChanged();
                }

//                expListAdapter = new ExpandableListAdapter(PortfolioView.this, SubcategoryList, subgroupCollection);
//                expListView.setAdapter(expListAdapter);
//                expListAdapter.notifyDataSetChanged();
            }
        }
    }

    //**********************************************************************************************
    //***   Method: Refresh Card Layout
    //**********************************************************************************************
    private void refreshView(View view) {

        //------------------------------------------------------------------------------------------
        //---   Update Category Header
        //------------------------------------------------------------------------------------------

//        TextView txtCategory = (TextView) view.findViewById(R.id.txtCategory);
//        txtCategory.setText(Category + " (" + allSubStocks.size() + ")");

        //------------------------------------------------------------------------------------------
        //---   Layout
        //------------------------------------------------------------------------------------------

        expListView = (ExpandableListView) view.findViewById(R.id.StockExpandList);
    }

    //**********************************************************************************************
    //***   Method: Refresh Card Layout
    //**********************************************************************************************
    private void refreshCard () {

//        allSubStocks = sqlHandler.getStocksByCategory(Category);

        createSubcategoryList();
        createSubgroupList();

        expListAdapter = new ExpandableListAdapter(PortfolioView.this, SubcategoryList, subgroupCollection);
        expListView.setAdapter(expListAdapter);
        expListAdapter.notifyDataSetChanged();
    }
}