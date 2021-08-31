package com.mairyu.app.kabukabu;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static android.util.Log.i;
import static com.mairyu.app.kabukabu.R.id.menu_refresh;
import static java.lang.StrictMath.abs;

//==================================================================================================
//===   PortfolioView
//==================================================================================================
public class PortfolioView extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Stock> allStocks = new ArrayList<>();
    ArrayList<Stock> RecyclerViewStocks = new ArrayList<>();

    private ArrayList<Integer> RecyclerViewItemType = new ArrayList<>();
    private ArrayList<Integer> RecyclerViewCategorySize = new ArrayList<>();
    HashMap<String, Boolean> SubCategoryToggleStatus = new HashMap<String, Boolean>();

    private CustomRecyclerViewAdapter adapterStocks;
    private RecyclerView rcvPortfolio;
    private ArrayAdapter<String> Portfolio_SubCategory_Adapter;

    private String[] MainCategoryArray;  // only used in spinner it seems

    private ArrayList<String> MainCategoryArrayList;
    private ArrayList<ArrayList<String>> SubCategoryArrayList = new ArrayList<>();

    String CurrentMainCategory;

//    private ArrayList<String> SubCategoryArrayLookUp;

//    private ArrayList<Boolean> MainCategoryExpand = new ArrayList<>();
//    private ArrayList<Boolean> SubCategoryExpand = new ArrayList<>();

//    Map<String, String> Sub2MainMap = new HashMap<>();

    EditText edtPortfolioTicker;
    EditText edtPortfolioShares;
    EditText edtPortfolioBasis;
    EditText edtPortfolioComission;

    Button btnPortfolioViewPagerAdd;

    private Button btnPortfolioAdd;
    private Button btnDatabaseSave;
    private Button btnPortfolioFilterShares;
    private Button btnPortfolioFilterPerc;

    private TextView portfolioListViewChangePerc;

    private Toolbar mToolbar;

//    Spinner PortfolioMainSpinner;

    private PreferenceSettings _appPrefs;

    private ViewPager pgrPortfolioView;
    private PagerAdapter PortfolioViewAdapter;

    TextView txtMainCategoryRecycleViewName;

    SQLStockHandler sqlStockHandler;
    int SQLDBSize;
    String Category;
    String PortfolioCategory;
    String PortfolioSubCategory;

    int FragmentCardIndex;
    int LastFragmentCardIndex;

    private boolean ViewExpand = false;

    private final static String TAG = MainActivity.class.getSimpleName();

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] navigation;

    private static int MAIN_CATEGORY = 1;
    private static int SUB_CATEGORY = 2;
    private static int STOCK_CATEGORY = 3;

    private static boolean COLLAPSED = false;
    private static boolean EXPANDED = true;

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

    //http://www.nasdaq.com/aspx/flashquotes.aspx?symbol=
    //http://www.nasdaq.com/symbol/aapl/option-chain
//    https://finance.yahoo.com/calendar/earnings?&day=2017-11-14
//    https://eresearch.fidelity.com/eresearch/conferenceCalls.jhtml?tab=earnings&begindate=11/14/2017
//    http://www.nasdaq.com/earnings/earnings-calendar.aspx?date=2017-Nov-14
//    https://www.etrade.wallst.com/v1/stocks/multisnapshot/multisnapshot.asp?symbols=NVDA,%20OPGSX,AWTAX,AMD,AMZN,NFLX,A,QCOM,LULU&peer=false

    //**********************************************************************************************
    //***   onCreate
    //**********************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.portfolio_view_pager);

        //------------------------------------------------------------------------------------------
        //---   Preference/Settings
        //------------------------------------------------------------------------------------------

        _appPrefs = new PreferenceSettings(getApplicationContext());

        //------------------------------------------------------------------------------------------
        //---   Toolbar
        //------------------------------------------------------------------------------------------

        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        //------------------------------------------------------------------------------------------
        //---   from 'strings.xml'
        //------------------------------------------------------------------------------------------
        //---   MainCategoryArrayList = {"INDEX ETF","REGION ETF"}
        //---   SubCategoryArrayList = {{"DOW","NASDAQ"...} {...}} -> [0] for"INDEX ETF", [1] for "REGION ETF" ...
        //------------------------------------------------------------------------------------------
        //---   Populate Main and Subcategory LUTs
        //------------------------------------------------------------------------------------------

//        MainCategoryArray = getResources().getStringArray(R.array.main_categories);
        MainCategoryArrayList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.main_categories)));

        for(String MainCategory : MainCategoryArrayList){

            try {

                SubCategoryArrayList.add(new ArrayList<>(Arrays.asList(getResources().getStringArray(getResources().getIdentifier(MainCategory.toLowerCase().replace(" ","_"), "array", getPackageName())))));

            } catch (Resources.NotFoundException e){

                Log.e(TAG, e.toString());
            }
        }

        //------------------------------------------------------------------------------------------
        //---   HASH MAP: {"DOW" <-> false} {"NASDAQ" <-> false} ...
        //------------------------------------------------------------------------------------------
        //---   Initialize hash map to all collapsed (false)
        //------------------------------------------------------------------------------------------

        for(ArrayList<String> SubCategory : SubCategoryArrayList) {

            for(String Company : SubCategory) {

//                SubCategoryArrayLookUp.add(Company);

                SubCategoryToggleStatus.put(Company, COLLAPSED);

//                Stock HeaderStock = new Stock();
//
//                HeaderStock.setTicker(Company);
//
//                RecyclerViewStocks.add(HeaderStock);
            }
        }

        //------------------------------------------------------------------------------------------
        //---   Populate Expand/Collapse LUTs
        //------------------------------------------------------------------------------------------

//        int SubIndexCounter = 0;
//
////        MainCategoryExpand.clear();
////        SubCategoryExpand.clear();
//
//        for (int MainIndex = 0; MainIndex < MainCategoryArrayList.size(); MainIndex = MainIndex + 1) {
//
//            MainCategoryExpand.add(MainIndex,false);
//
//            for (int SubIndex = 0; SubIndex < SubCategoryArrayList.get(MainIndex).size(); SubIndex = SubIndex + 1) {
//
//                SubCategoryExpand.add(SubIndexCounter,false);
//
////                Sub2MainMap.put(SubCategoryArrayLookUp.get(SubIndex),MainCategoryArrayList.get(MainIndex));
//            }
//        }

        //------------------------------------------------------------------------------------------
        //---   SQLite Setup
        //------------------------------------------------------------------------------------------

        sqlStockHandler = new SQLStockHandler(PortfolioView.this,_appPrefs.getSQLStockDBName(),
                Integer.parseInt(_appPrefs.getSQLStockDBVersion()));

        //------------------------------------------------------------------------------------------
        //---   ViewPager
        //------------------------------------------------------------------------------------------

        pgrPortfolioView = findViewById(R.id.pgrPortfolioView);
        PortfolioViewAdapter = new CardViewPagerAdapter(getSupportFragmentManager());
        pgrPortfolioView.setAdapter(PortfolioViewAdapter);
        PortfolioViewAdapter.notifyDataSetChanged();

        //----------------------------------------------------------------------------------
        //---   Navigation Drawer
        //----------------------------------------------------------------------------------

        navigation = getResources().getStringArray(R.array.navigation);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,navigation));
        mDrawerList.setOnItemClickListener(new Navigator(this));

        //------------------------------------------------------------------------------------------
        //---   Show initial card and setup layout after everything is stable
        //------------------------------------------------------------------------------------------

        pgrPortfolioView.post(new Runnable() {

            public void run() {

                btnPortfolioViewPagerAdd = findViewById(R.id.btnPortfolioViewPagerAdd);
                btnPortfolioViewPagerAdd.setOnClickListener(PortfolioView.this);

                CurrentMainCategory = MainCategoryArrayList.get(0);

                TextView txtCategory = findViewById(R.id.txtCategory);

                txtCategory.setText(CurrentMainCategory);

                RecyclerViewStocks = sqlStockHandler.getStocksByCategory("INDEX ETF","MISC");

                //----------------------------------------------------------------------------------
                //---   Refresh Input Array for RecyclerView accounting for expand/collapse
                //----------------------------------------------------------------------------------

                RefreshRecyclerArray();

                //----------------------------------------------------------------------------------
                //---   Why hardcoded to '0'???
                //----------------------------------------------------------------------------------

                View Fragmentview = ((CardViewPagerAdapter) pgrPortfolioView.getAdapter()).getFragment(0).getView();

                RefreshView(Fragmentview);

                //----------------------------------------------------------------------------------
                //---   ViewPager Listener
                //----------------------------------------------------------------------------------

                pgrPortfolioView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                    // triggered many many times when touching and trying to scroll it, once released it finishes with new position
                    @Override
                    public void onPageScrolled(int CardPosition, float positionOffset, int positionOffsetPixels) {

                    }

                    //------------------------------------------------------------------------------
                    //---   Page Slide (left or right)
                    //------------------------------------------------------------------------------
                    @Override
                    public void onPageSelected(int position) {

                        Log.i("LOG: (PV) PageViewer", "Position: " + position);


                        //--------------------------------------------------------------------------
                        //---   Reshuffle
                        //--------------------------------------------------------------------------
                        if (LastFragmentCardIndex == -1) {

                            FragmentCardIndex = 0;

                        //--------------------------------------------------------------------------
                        //---   Slide right (... go left ...)
                        //--------------------------------------------------------------------------
                        } else if (LastFragmentCardIndex > position) {

                            FragmentCardIndex--;

                        //--------------------------------------------------------------------------
                        //---   Slide left (... go right ...)
                        //--------------------------------------------------------------------------
                        } else {

                            FragmentCardIndex++;
                        }

                        LastFragmentCardIndex = position;

                        CurrentMainCategory = MainCategoryArrayList.get(FragmentCardIndex);

                        txtCategory.setText(CurrentMainCategory + " - " + position + " - " + FragmentCardIndex );

                        RefreshRecyclerArray();

                        View Fragmentview = ((CardViewPagerAdapter) pgrPortfolioView.getAdapter()).getFragment(position).getView();

                        RefreshView(Fragmentview);

//                        Category = MainCategoryArrayList.get(position);

//                        txtMainCategoryRecycleViewName.setText(MainCategoryArrayList.get(position));

//                        currentSQLIndex = cardIndexArray.get(position);
//                        refreshCard(currentSQLIndex);
//
//                        txtCardProgress.setText((position+1) + " / " + CardBatchSize);

                    }

                    //SCROLL_STATE_IDLE=0, SCROLL_STATE_DRAGGING=1, SCROLL_STATE_SETTLING=2
                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        });
    }

    //**********************************************************************************************
    //***   Page Adapter (FragmentStatePagerAdapter caused weirdness in HashMap)
    //**********************************************************************************************
    private class CardViewPagerAdapter extends FragmentPagerAdapter {

        HashMap<Integer,String> mFragmentTags = new HashMap<Integer,String>();

        public CardViewPagerAdapter(FragmentManager fm) {

            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return new PortfolioViewFragment();
        }

        @Override
        public int getCount() {

            return 3;
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
    //***   Method: Refresh Card Layout
    //**********************************************************************************************
    private void RefreshView(View view) {

        //------------------------------------------------------------------------------------------
        //---   Layout
        //------------------------------------------------------------------------------------------

//        txtMainCategoryRecycleViewName = view.findViewById(R.id.txtMainCategoryRecycleViewName);

        //------------------------------------------------------------------------------------------
        //---   RecyclerView Setup
        //------------------------------------------------------------------------------------------

        rcvPortfolio= view.findViewById(R.id.rcvPortfolio);
        rcvPortfolio.setLayoutManager(new LinearLayoutManager(PortfolioView.this));

        adapterStocks = new CustomRecyclerViewAdapter(PortfolioView.this, RecyclerViewStocks);
        rcvPortfolio.setAdapter(adapterStocks);
    }

    //**********************************************************************************************
    //***   Custom Array Adapter for RecyclerView
    //**********************************************************************************************
    private class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter.ViewHolder> {

        private ArrayList<Stock> mData;
        private LayoutInflater mInflater;

        //------------------------------------------------------------------------------------------
        //---   Constructor
        //------------------------------------------------------------------------------------------
        CustomRecyclerViewAdapter(Context context, ArrayList<Stock> data) {

            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        //------------------------------------------------------------------------------------------
        //---   Inflates the row layout from xml when needed
        //------------------------------------------------------------------------------------------
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = mInflater.inflate(R.layout.portfolio_stock_recycle_view_item, parent, false);

            return new ViewHolder(view);
        }

        //------------------------------------------------------------------------------------------
        //---   Number of rows
        //------------------------------------------------------------------------------------------
        @Override
        public int getItemCount() {

            return mData.size();
        }

        //------------------------------------------------------------------------------------------
        //---   Binds corresponding data to the TextView in each row
        //------------------------------------------------------------------------------------------
        @Override
        public void onBindViewHolder(ViewHolder holder, final int RecyclerViewPosition) {

            Stock CurrentStock = mData.get(RecyclerViewPosition);

            holder.txtPortfolioViewTicker.setText(CurrentStock.getTicker());
            holder.txtPortfolioViewCompany.setText(CurrentStock.getCompany());
//            holder.txtPortfolioViewGainLoss.setText(CurrentStock.get());

//            holder.txtPortfolioViewPrice.setText("$" + CurrentStock.getBasis());
//            holder.portfolioListViewChangePerc.setText("$" + CurrentStock.getBasis());
            holder.txtPortfolioViewTransactionBasis.setText("$" + CurrentStock.getBasis());
            holder.txtPortfolioViewTransactionDate.setText(CurrentStock.getDate());

            //--------------------------------------------------------------------------------------
            //---   Subcategory Header ...
            //--------------------------------------------------------------------------------------
            if (CurrentStock.getCategory().equals("MAIN HEADER")) {

                holder.txtPortfolioViewTicker.setVisibility(View.VISIBLE);
                holder.txtPortfolioViewCompany.setVisibility(View.GONE);
                holder.txtPortfolioViewGainLoss.setVisibility(View.GONE);
                holder.txtPortfolioViewPrice.setVisibility(View.GONE);
                holder.portfolioListViewChangePerc.setVisibility(View.GONE);

                holder.txtPortfolioViewTransactionBasis.setVisibility(View.GONE);
                holder.txtPortfolioViewTransactionDate.setVisibility(View.GONE);

            //--------------------------------------------------------------------------------------
            //---   Stock Info ...
            //--------------------------------------------------------------------------------------
            } else {

                holder.txtPortfolioViewTicker.setVisibility(View.VISIBLE);
                holder.txtPortfolioViewCompany.setVisibility(View.VISIBLE);
                holder.txtPortfolioViewGainLoss.setVisibility(View.VISIBLE);
                holder.txtPortfolioViewPrice.setVisibility(View.VISIBLE);
                holder.portfolioListViewChangePerc.setVisibility(View.VISIBLE);

                holder.txtPortfolioViewTransactionBasis.setVisibility(View.VISIBLE);
                holder.txtPortfolioViewTransactionDate.setVisibility(View.VISIBLE);
            }
        }

        //------------------------------------------------------------------------------------------
        //---   Triggered when a row crosses a different row
        //------------------------------------------------------------------------------------------
        public void onRowMoved(int fromPosition, int toPosition, ViewHolder myViewHolder) {

        }

        //------------------------------------------------------------------------------------------
        //---   When a row is long-clicked prior to drag ...
        //------------------------------------------------------------------------------------------
        public void onRowSelected(ViewHolder myViewHolder, int ActionState) {

        }

        //------------------------------------------------------------------------------------------
        //---   When a row is released during drag ...
        //------------------------------------------------------------------------------------------
        public void onDragRelease(ViewHolder myViewHolder) {

        }

        //------------------------------------------------------------------------------------------
        //---   Delete Yes/No Dialogue
        //------------------------------------------------------------------------------------------
        public void onPopUpAlert(final ViewHolder myViewHolder, int position) {

//            AlertDialog.Builder SwipeAlert = new AlertDialog.Builder(myViewHolder.itemView.getContext());
//            SwipeAlert.setTitle("DELETE?");
//            SwipeAlert.setMessage("Are you sure?");
//
//            SwipeAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//
//                    int position = myViewHolder.getAdapterPosition();
//
//                    sqlBookHandler.deleteBook(RecyclerViewBooks.get(position));
//
//                    RefreshRecycler();
//                }
//            });
//
//            SwipeAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//
//                    RefreshRecycler();
//                }
//            });
//
//            SwipeAlert.show();
        }

        //******************************************************************************************
        //***   [INNER CLASS]
        //******************************************************************************************
        //---   Stores, defines and recycles (sub)views as they are scrolled off screen
        //------------------------------------------------------------------------------------------
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener  {

            TextView txtPortfolioViewTicker;
            TextView txtPortfolioViewCompany;
            TextView txtPortfolioViewGainLoss;
            TextView txtPortfolioViewPrice;
            TextView portfolioListViewChangePerc;
            TextView txtPortfolioViewTransactionBasis;
            TextView txtPortfolioViewTransactionDate;

            ViewHolder(View itemView) {

                super(itemView);

                txtPortfolioViewTicker = itemView.findViewById(R.id.txtPortfolioViewTicker);
                txtPortfolioViewCompany = itemView.findViewById(R.id.txtPortfolioViewCompany);
                txtPortfolioViewGainLoss = itemView.findViewById(R.id.txtPortfolioViewGainLoss);
                txtPortfolioViewPrice = itemView.findViewById(R.id.txtPortfolioViewPrice);
                portfolioListViewChangePerc = itemView.findViewById(R.id.portfolioListViewChangePerc);
                txtPortfolioViewTransactionBasis = itemView.findViewById(R.id.txtPortfolioViewTransactionBasis);
                txtPortfolioViewTransactionDate = itemView.findViewById(R.id.txtPortfolioViewTransactionDate);

                //----------------------------------------------------------------------------------

                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            //--------------------------------------------------------------------------------------
            //---   Short Click  ...
            //--------------------------------------------------------------------------------------
            @Override
            public void onClick(View view) {

                Stock CurrentStock =  RecyclerViewStocks.get(getAdapterPosition());

                //----------------------------------------------------------------------------------
                //---   Expand / Collapse Group
                //----------------------------------------------------------------------------------
                if (CurrentStock.getCategory().equals("MAIN HEADER")) {

                    //------------------------------------------------------------------------------
                    //---   Main Header ...
                    //------------------------------------------------------------------------------
//                    if (CurrentStock.getCompany().equals("N/A")) {

                        String ClickSubCategory = CurrentStock.getTicker();

                        if (SubCategoryToggleStatus.get(ClickSubCategory) == COLLAPSED) {

                            SubCategoryToggleStatus.put(ClickSubCategory, EXPANDED);

                        } else {

                            SubCategoryToggleStatus.put(ClickSubCategory, COLLAPSED);
                        }

//                    //------------------------------------------------------------------------------
//                    //---   Sub Header ...
//                    //------------------------------------------------------------------------------
//                    } else {
//
//                    }

                    RefreshRecyclerArray();

                    View Fragmentview = ((CardViewPagerAdapter) pgrPortfolioView.getAdapter()).getFragment(0).getView();

                    RefreshView(Fragmentview);
                }
            }

            //--------------------------------------------------------------------------------------
            //---   Long CLick ...
            //--------------------------------------------------------------------------------------
            @Override
            public boolean onLongClick(View view) {

                return true;
            }
        }

//        // convenience method for getting data at click position
//        Stock getItem(int id) {
//
//            return mData.get(id);
//        }
    }

    //**********************************************************************************************
    //***   Method: Refresh Input Array for RecyclerView accounting for expand/collapse
    //**********************************************************************************************
    private void RefreshRecyclerArray() {

        //------------------------------------------------------------------------------------------
        //---   Reset input array for fresh start ...
        //------------------------------------------------------------------------------------------

        RecyclerViewStocks.clear();

        //------------------------------------------------------------------------------------------
        //---   Loop through all subcategories
        //------------------------------------------------------------------------------------------
        for (String tmpSubCategory : SubCategoryArrayList.get(FragmentCardIndex)) {

            Stock HeaderStock = new Stock();

            HeaderStock.setTicker(tmpSubCategory);
            HeaderStock.setCategory("MAIN HEADER");
            HeaderStock.setSubcategory(tmpSubCategory);

            RecyclerViewStocks.add(HeaderStock);

            for (Stock LoopStock : sqlStockHandler.getStocksByCategory(CurrentMainCategory, tmpSubCategory)) {

                if (SubCategoryToggleStatus.get(tmpSubCategory) == EXPANDED) {

                    RecyclerViewStocks.add(LoopStock);
                }
            }
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
            case R.id.btnPortfolioViewPagerAdd:

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
                //---   Main Category Spinner
                //----------------------------------------------------------------------------------

                Spinner PortfolioMainSpinner = popupView.findViewById(R.id.spnPortfolioCategory);
                PortfolioMainSpinner.setOnItemSelectedListener(new PortfolioMainCategoryOnItemSelectedListener());

                ArrayAdapter<CharSequence> MainCategoryAdapter = ArrayAdapter.createFromResource(this,
                        R.array.main_categories, android.R.layout.simple_spinner_item);

                MainCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                PortfolioMainSpinner.setAdapter(MainCategoryAdapter);
                PortfolioMainSpinner.setSelection(MainCategoryArrayList.indexOf(CurrentMainCategory));

                //----------------------------------------------------------------------------------
                //---   Sub Category Spinner
                //----------------------------------------------------------------------------------

                Spinner PortfolioSubSpinner = popupView.findViewById(R.id.spnPortfolioSubCategory);
                PortfolioSubSpinner.setOnItemSelectedListener(new PortfolioSubCategoryOnItemSelectedListener());

                ArrayAdapter<CharSequence> SubcatgoryAdapter =  new ArrayAdapter(this,
                        android.R.layout.simple_spinner_item, SubCategoryArrayList.get(FragmentCardIndex));

                SubcatgoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                PortfolioSubSpinner.setAdapter(SubcatgoryAdapter);
                PortfolioSubSpinner.setSelection(0);

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

                        sqlStockHandler.addStock(newStock);

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

//                Log.i("LOG: (PF) SHARES", "Count: " + adapterStocks.getCount());

                //------------------------------------------------------------------------------
                //---   Remove filtered ones from ListView
                //------------------------------------------------------------------------------
                heap = 0;

//                for (int i : FilterShares) {
//
//                    Stock toRemove = adapterStocks.getItem(i-heap);
//                    adapterStocks.remove(toRemove);
//                    heap++;
//                }

                break;

            //--------------------------------------------------------------------------------------
            //---   Filter < 1% Gain/Loss
            //--------------------------------------------------------------------------------------
            case R.id.btnPortfolioFilterPerc:

//                position = 0;
//
//                ArrayList<Integer> FilterPerc = new ArrayList<Integer>();
//
//                for (Stock stock : allStocks) {
//
//                    if (abs(Float.parseFloat(stock.getChangePerc().replace("%",""))) > 1) {
//
//                        Log.i("LOG: (PF) PERC", "Perc: " + stock.getTicker());
//
//                    } else {
//
//                        FilterPerc.add(position);
//                    }
//                    position++;
//                }
//
//                Log.i("LOG: (PF) PERC", "Count: " + adapterStocks.getCount());
//
//                heap = 0;
//
//                for (int i : FilterPerc) {
//
//                    Log.i("LOG: (PF) PERC", "Perc: " + i + (i-heap));
//
//                    Stock toRemove = adapterStocks.getItem(i-heap);
//                    adapterStocks.remove(toRemove);
//                    heap++;
//                }

                break;
        }
    }

    //**********************************************************************************************
    //***   CustomOnItemSelectedListener (for Spinner)
    //**********************************************************************************************
    public class PortfolioMainCategoryOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {

            PortfolioCategory = parent.getItemAtPosition(pos).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

            Log.i("LOG: (PV) Pull", "NothingMain: " + arg0);;
        }
    }

    public class PortfolioSubCategoryOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {

            PortfolioSubCategory = parent.getItemAtPosition(pos).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

            Log.i("LOG: (PV) Pull", "NothingSub: " + arg0);;
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

        //------------------------------------------------------------------------------------------
        //---   Show active Category
        //------------------------------------------------------------------------------------------

        TextView txtCategory = (TextView) findViewById(R.id.txtCategory);
//        txtCategory.setText(Category + " (" + SQLDBSize + ")");
//        txtCategory.setText("PORTFOLIO");
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
}