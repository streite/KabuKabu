/*
package com.mairyu.app.kabukabu;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import java.util.Map;

import static android.util.Log.i;
import static com.mairyu.app.kabukabu.R.id.menu_refresh;
import static java.lang.StrictMath.abs;

//==================================================================================================
//===   PortfolioView
//==================================================================================================
public class PortfolioView_old extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Stock> allStocks = new ArrayList<>();
    ArrayList<Stock> RecyclerViewStocks = new ArrayList<>();

    private ArrayList<Stock> RecyclerViewStockArray = new ArrayList<>();
    private ArrayList<Integer> RecyclerViewItemType = new ArrayList<>();
    private ArrayList<Integer> RecyclerViewCategorySize = new ArrayList<>();

    private CustomRecyclerViewAdapter adapterStocks;
    private RecyclerView rcvPortfolio;
    private ArrayAdapter<String> Portfolio_SubCategory_Adapter;

    private String[] MainCategoryArray;  // only used in spinner it seems

    private ArrayList<String> MainCategoryArrayList;
    private ArrayList<ArrayList<String>> SubCategoryArrayList;

    private ArrayList<String> SubCategoryArrayLookUp;

    private ArrayList<Boolean> MainCategoryExpand = new ArrayList<>();
    private ArrayList<Boolean> SubCategoryExpand = new ArrayList<>();

    Map<String, String> Sub2MainMap = new HashMap<>();

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

    private Spinner Portfolio_Spinner;

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
        //---   Define Subcategories
        //------------------------------------------------------------------------------------------


        //------------------------------------------------------------------------------------------
        //---   Get Card Details
        //------------------------------------------------------------------------------------------

//        Bundle extras = getIntent().getExtras();
//
//        Category = "IPO";

        //------------------------------------------------------------------------------------------
        //---   from 'strings.xml'
        //------------------------------------------------------------------------------------------

        MainCategoryArray = getResources().getStringArray(R.array.main_categories);
        MainCategoryArrayList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.main_categories)));
        SubCategoryArrayLookUp = new ArrayList<String>();

        ArrayList<String> mainCategoryArray;
        SubCategoryArrayList  = new ArrayList<>();

        for(String MainCategory : MainCategoryArrayList){

            try {
                SubCategoryArrayList.add(new ArrayList<>(Arrays.asList(getResources().getStringArray(getResources().getIdentifier(MainCategory.toLowerCase().replace(" ","_"), "array", getPackageName())))));
            } catch (Resources.NotFoundException e){
                Log.e(TAG, e.toString());
            }
        }

        for(ArrayList<String> SubCategory : SubCategoryArrayList) {

            for(String Company : SubCategory) {

                SubCategoryArrayLookUp.add(Company);

//                Stock HeaderStock = new Stock();
//
//                HeaderStock.setTicker(Company);
//
//                RecyclerViewStocks.add(HeaderStock);
            }
        }

        //------------------------------------------------------------------------------------------
        //---   Populate
        //------------------------------------------------------------------------------------------

        int SubIndexCounter = 0;

//        MainCategoryExpand.clear();
//        SubCategoryExpand.clear();

        for (int MainIndex = 0; MainIndex < MainCategoryArrayList.size(); MainIndex = MainIndex + 1) {

            MainCategoryExpand.add(MainIndex,false);

            for (int SubIndex = 0; SubIndex < SubCategoryArrayList.get(MainIndex).size(); SubIndex = SubIndex + 1) {

                SubCategoryExpand.add(SubIndexCounter,false);

                Sub2MainMap.put(SubCategoryArrayLookUp.get(SubIndex),MainCategoryArrayList.get(MainIndex));
            }
        }

        //------------------------------------------------------------------------------------------
        //---   SQLite Setup
        //------------------------------------------------------------------------------------------

        sqlStockHandler = new SQLStockHandler(PortfolioView_old.this,_appPrefs.getSQLStockDBName(),
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

                btnPortfolioViewPagerAdd = (Button) findViewById(R.id.btnPortfolioViewPagerAdd);
                btnPortfolioViewPagerAdd.setOnClickListener(PortfolioView_old.this);

                RecyclerViewStocks = sqlStockHandler.getStocksByCategory("INDEX ETF","MISC");

                View Fragmentview = ((CardViewPagerAdapter) pgrPortfolioView.getAdapter()).getFragment(0).getView();

                RefreshRecyclerArray();

                RefreshView(Fragmentview);

                //----------------------------------------------------------------------------------
                //---   ViewPager Listener
                //----------------------------------------------------------------------------------

                pgrPortfolioView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                    // triggered many many times when touching and trying to scroll it, once released it finishes with new position
                    @Override
                    public void onPageScrolled(int CardPosition, float positionOffset, int positionOffsetPixels) {

                    }

                    //--------------------------------------------------------------------------------------
                    //---   Page Slide (left or right)
                    //--------------------------------------------------------------------------------------
                    @Override
                    public void onPageSelected(int position) {

                        //------------------------------------------------------------------------------
                        //---   Reshuffle
                        //------------------------------------------------------------------------------
                        if (LastFragmentCardIndex == -1) {

                            FragmentCardIndex = 0;

                            //------------------------------------------------------------------------------
                            //---   Slide right (... go left ...)
                            //------------------------------------------------------------------------------
                        } else if (LastFragmentCardIndex > position) {

                            FragmentCardIndex--;

                            //------------------------------------------------------------------------------
                            //---   Slide left (... go right ...)
                            //------------------------------------------------------------------------------
                        } else {

                            FragmentCardIndex++;
                        }

                        LastFragmentCardIndex = position;

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

        //------------------------------------------------------------------------------------------
        //---   Show initial card
        //------------------------------------------------------------------------------------------

//        prepareRecyclerViewContent();
//
//        rcvPortfolioViewMainCategory = findViewById(R.id.rcvPortfolioViewMainCategory);
//        rcvPortfolioViewMainCategory.setLayoutManager(new LinearLayoutManager(PortfolioView.this));
//
//        findViewById(R.id.clPortfolioView).post(new Runnable() {
//
//            public void run() {
//
//                adapterStocks = new CustomRecyclerViewAdapter(PortfolioView.this, RecyclerViewStockArray);
//                rcvPortfolioViewMainCategory.setAdapter(adapterStocks);
//            }
//        });
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

        rcvPortfolio= findViewById(R.id.rcvPortfolio);
        rcvPortfolio.setLayoutManager(new LinearLayoutManager(PortfolioView_old.this));

        adapterStocks = new CustomRecyclerViewAdapter(PortfolioView_old.this, RecyclerViewStocks);
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

            holder.txtPortfolioViewTransactionBasis.setText("$" + CurrentStock.getBasis());
            holder.txtPortfolioViewTransactionDate.setText(CurrentStock.getDate());

            //--------------------------------------------------------------------------------------
            //---   Author Header
            //--------------------------------------------------------------------------------------
            if (CurrentStock.getCompany().equals("N/A")) {

                holder.txtPortfolioViewTicker.setVisibility(View.VISIBLE);

            //--------------------------------------------------------------------------------------
            //---   Title Row
            //--------------------------------------------------------------------------------------
            } else {

                holder.txtPortfolioViewTicker.setVisibility(View.GONE);
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
            TextView txtPortfolioViewTransactionBasis;
            TextView txtPortfolioViewTransactionDate;

            ViewHolder(View itemView) {

                super(itemView);

                txtPortfolioViewTicker = itemView.findViewById(R.id.txtPortfolioViewTicker);
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

                //----------------------------------------------------------------------------------
                //---   Expand / Collapse Group
                //----------------------------------------------------------------------------------
//                if (RecyclerViewBooks.get(getAdapterPosition()).getTitle().equals("N/A")) {
//
//                    String ClickAuthor = RecyclerViewBooks.get(getAdapterPosition()).getAuthor();
//
//                    if (AuthorToggleStatus.get(ClickAuthor) == COLLAPSED) {
//
//                        AuthorToggleStatus.put(ClickAuthor,EXPANDED);
//
//                    } else {
//
//                        AuthorToggleStatus.put(ClickAuthor,COLLAPSED);
//                    }
//
//                    RefreshRecyclerArray();
//
//                    RefreshRecycler();
//                }
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

        for (String AuthorName : SubCategoryArrayLookUp) {

            Stock HeaderStock = new Stock();

            HeaderStock.setTicker("NASDAQ");

            RecyclerViewStocks.add(HeaderStock);

            int SubIndex = SubCategoryArrayLookUp.indexOf(AuthorName);

            for (Stock LoopStock : sqlStockHandler.getStocksByCategory("INDEX ETF", "MISC")) {

                if (LoopStock.getCompany().equals("N/A")) {

                    RecyclerViewStocks.add(LoopStock);

                } else if (SubCategoryExpand.get(SubIndex) == EXPANDED) {

                    RecyclerViewStocks.add(LoopStock);
                }
            }
        }
    }

//    //**********************************************************************************************
//    //***   Custom Recycler View Adapter
//    //**********************************************************************************************
//    private class CustomRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//        private LayoutInflater mInflater;
//        private ArrayList<Stock> mData;
//
//        //------------------------------------------------------------------------------------------
//        //---   Constructor
//        //------------------------------------------------------------------------------------------
//        CustomRecyclerViewAdapter(Context context, ArrayList<Stock> data) {
//
//            this.mInflater = LayoutInflater.from(context);
//            this.mData = data;
//        }
//
//        // inflates the row layout from xml when needed
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//            RecyclerView.ViewHolder viewHolder;
//            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//
//            switch (viewType) {
//                case 1:
//                    View v1 = inflater.inflate(R.layout.main_category_recycle_view_item, parent, false);
//                    viewHolder = new ViewHolderCategory(v1);
//                    break;
//                case 2:
//                    View v2 = inflater.inflate(R.layout.sub_category_recycle_view_item, parent, false);
//                    viewHolder = new ViewHolderCategory(v2);
//                    break;
//                case 3:
//                    View v3 = inflater.inflate(R.layout.portfolio_stock_recycle_view_item, parent, false);
//                    viewHolder = new ViewHolderBase(v3);
//                    break;
//                default:
//                    View v4 = inflater.inflate(R.layout.portfolio_stock_recycle_view_expanded_item, parent, false);
//                    viewHolder = new ViewHolderBase(v4);
//                    break;
//            }
//            return viewHolder;
//        }
//
//        // binds the data to the TextView in each row
//        @Override
//        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//
//            switch (holder.getItemViewType()) {
//
//                case 1:
//
//                    ViewHolderCategory vh1 = (ViewHolderCategory) holder;
//                    configureViewHolder1(vh1, position);
//                    break;
//                case 2:
//                    ViewHolderCategory vh2 = (ViewHolderCategory) holder;
//                    configureViewHolder2(vh2, position);
//                    break;
//                default:
//                    ViewHolderBase vh3 = (ViewHolderBase) holder;
//                    configureViewHolder3(vh3, position);
//
//                    break;
//            }
//        }
//
//        // Allow for conditional XMLs for the row views
//        @Override
//        public int getItemViewType(int position) {
//
//            return RecyclerViewItemType.get(position);
//        }
//
//        // total number of rows
//        @Override
//        public int getItemCount() {
//
//            return mData.size();
//        }
//
//        public void updateData(ArrayList<Stock> viewModels) {
//
//            mData.clear();
//            mData.addAll(viewModels);
//            notifyDataSetChanged();
//        }
//
//        private void configureViewHolder1(ViewHolderCategory vh1, int position) {
//
//            Stock RecycleStock = mData.get(position);
//
////            int MainIndex = MainCategoryArrayList.indexOf(RecycleStock.getCompany());
//
//            vh1.myTextView.setText(RecycleStock.getCompany() + " (" + RecyclerViewCategorySize.get(position) + ")");
//        }
//        private void configureViewHolder2(ViewHolderCategory vh2, int position) {
//
//            Stock RecycleStock = mData.get(position);
//
////            int MainIndex = MainCategoryArrayList.indexOf(RecycleStock.getCompany());
////            int SubIndex = SubCategoryArrayList.get(MainCategoryArrayList.indexOf(MainIndex)).indexOf(RecycleStock.getCompany());
//
//            vh2.myTextView.setText(RecycleStock.getCompany() + " (" + RecyclerViewCategorySize.get(position) + ")");
//        }
//        private void configureViewHolder3(ViewHolderBase vh3, int position) {
//
//            Stock RecycleStock = mData.get(position);
//
//            vh3.txtPortfolioViewTicker.setText(RecycleStock.getTicker());
//            vh3.txtPortfolioViewCompany.setText(RecycleStock.getCompany());
//            vh3.txtPortfolioViewPrice.setText(RecycleStock.getPrice()+"");
//        }
//
//        //******************************************************************************************
//        //***   [INNER CLASS] - Main/Sub Category
//        //******************************************************************************************
//        // stores and recycles views as they are scrolled off screen
//        public class ViewHolderCategory extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener  {
//
//            TextView myTextView;
//            ImageView imgMainCategoryRecycleViewState;
//
//            //--------------------------------------------------------------------------------------
//            //---   Constructor
//            //--------------------------------------------------------------------------------------
//            ViewHolderCategory(View itemView) {
//
//                super(itemView);
//
////                myTextView = itemView.findViewById(R.id.txtMainCategoryRecycleViewName);
//                imgMainCategoryRecycleViewState = itemView.findViewById(R.id.imgMainCategoryRecycleViewState);
//
////                itemView.setOnCreateContextMenuListener(MainActivity.this);
//
////                imgMainCategoryRecycleViewState.setOnClickListener(this);
//
//                imgMainCategoryRecycleViewState.setOnClickListener(new View.OnClickListener() {
//
//                    //------------------------------------------------------------------------------
//                    //---
//                    //------------------------------------------------------------------------------
//                    @Override
//                    public void onClick(View view) {
//
//                        ImageView imgMainCategoryRecycleViewState = view.findViewById(R.id.imgMainCategoryRecycleViewState);
//
//                        Stock ClickStock = RecyclerViewStockArray.get(getAdapterPosition());
//
//                        //----------------------------------------------------------------------------------
//                        //---   Expand / Collapse
//                        //----------------------------------------------------------------------------------
//                        if (ClickStock.getCategory().equals("MAIN")) {
//
//                            int MainIndex = MainCategoryArrayList.indexOf(ClickStock.getCompany());
//
//                            if (MainCategoryExpand.get(MainIndex)) {
//
//                                MainCategoryExpand.set(MainIndex,false);
//
//                                imgMainCategoryRecycleViewState.setImageResource(R.drawable.ic_arrow_drop_up_white);
//
//                            } else {
//
//                                MainCategoryExpand.set(MainIndex,true);
//
//                                imgMainCategoryRecycleViewState.setImageResource(R.drawable.ic_arrow_drop_down_white);
//                            }
//
//                            prepareRecyclerViewContent();
//
//                            adapterStocks.notifyDataSetChanged();
//
//                        } else if (ClickStock.getCategory().equals("SUB")) {
//
//                            int SubIndex = SubCategoryArrayLookUp.indexOf(ClickStock.getCompany().replace("&", "_"));
//
//                            if (SubCategoryExpand.get(SubIndex)) {
//
//                                SubCategoryExpand.set(SubIndex,false);
//
//                                imgMainCategoryRecycleViewState.setImageResource(R.drawable.ic_arrow_drop_up_white);
//
//                            } else {
//
//                                SubCategoryExpand.set(SubIndex,true);
//
//                                imgMainCategoryRecycleViewState.setImageResource(R.drawable.ic_arrow_drop_down_white);
//                            }
//
//                            prepareRecyclerViewContent();
//
//                            adapterStocks.notifyDataSetChanged();
//                        }
//                    }
//                });
//
//                itemView.setOnClickListener(this);
//                itemView.setOnLongClickListener(this);
//            }
//
//            //--------------------------------------------------------------------------------------
//            //---   Short Click  ...
//            //--------------------------------------------------------------------------------------
//            @Override
//            public void onClick(View view) {
//
//            }
//
//            //--------------------------------------------------------------------------------------
//            //---   Long CLick ...
//            //--------------------------------------------------------------------------------------
//            @Override
//            public boolean onLongClick(View view) {
//
//                Intent intent2Info = new Intent(PortfolioView.this, AlpacaAPI.class);
//
////                intent2Info.putExtra("SQL_STOCK_ID", currentStock.getId());
//
//                //---   ==> CardView
//                startActivityForResult(intent2Info, REQUEST_INFO);
//
//                //----------------------------------------------------------------------------------
//                //---   SQL File Selection Popup
//                //----------------------------------------------------------------------------------
//
////                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
////                View popupView = layoutInflater.inflate(R.layout.confirm_popup_window,null);
////
////                popupWindow = new PopupWindow(popupView,CONFIRM_POPUP_WIDTH,CONFIRM_POPUP_HEIGHT);
////                popupWindow.showAtLocation(findViewById(R.id.clvMainActivity),
////                        Gravity.CENTER, CONFIRM_POPUP_GRAVITY_X, CONFIRM_POPUP_GRAVITY_Y);
////
////                CategorySpinner = popupView.findViewById(R.id.spnCategory);
////                CategorySpinner.setOnItemSelectedListener(new SpinnerCustomOnItemSelectedListener());
////                CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(CategorySpinner.getContext(), MainCategoryArrayList);
////                CategorySpinner.setAdapter(customSpinnerAdapter);
////
////                //----------------------------------------------------------------------------------
////                //---   Category Update Button
////                //----------------------------------------------------------------------------------
////
////                Button btnConfirmPopUpUpdate = popupView.findViewById(R.id.btnConfirmPopUpUpdate);
////
////                btnConfirmPopUpUpdate.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////
////                        String CurrentMainCategory = MainCategoryArray[CategorySpinner.getSelectedItemPosition()];
////
////                        Recipe tmpRecipe = RecyclerViewRecipes.get(getAdapterPosition());
////                        tmpRecipe.setCategory(CurrentMainCategory);
////
////                        sqlRecipeHandler.updateRecipe(tmpRecipe);
////
////                        categorizeRecipes();
////
////                        adapterAllRecipes.notifyDataSetChanged();
////
////                        popupWindow.dismiss();
////                    }
////                });
////
////                //----------------------------------------------------------------------------------
////                //---   Recipe Delete Button
////                //----------------------------------------------------------------------------------
////
////                Button btnConfirmPopUpDelete = popupView.findViewById(R.id.btnConfirmPopUpDelete);
////
////                btnConfirmPopUpDelete.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////
////                        sqlRecipeHandler.deleteRecipe(RecyclerViewRecipes.get(getAdapterPosition()));
////
////                        categorizeRecipes();
////
////                        adapterAllRecipes.notifyDataSetChanged();
////
////                        popupWindow.dismiss();
////                    }
////                });
////
////                //----------------------------------------------------------------------------------
////                //---   Swipe and close popUp
////                //----------------------------------------------------------------------------------
////
////                ConstraintLayout viewPutSleep = popupView.findViewById(R.id.clvConfirmPopUp);
////
////                viewPutSleep.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
////
////                    @Override
////                    public void onSwipeLeft() {
////
////                        popupWindow.dismiss();
////                    }
////
////                    @Override
////                    public void onSwipeRight() {
////
////                        popupWindow.dismiss();
////                    }
////                });
//
//                return true;
//            }
//        }
//
//        //******************************************************************************************
//        //***   [INNER CLASS] - Stock Item
//        //******************************************************************************************
//        // stores and recycles views as they are scrolled off screen
//        public class ViewHolderBase extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener  {
//
//            TextView txtPortfolioViewTicker;
//            TextView txtPortfolioViewCompany;
//            TextView txtPortfolioViewPrice;
//
//            //--------------------------------------------------------------------------------------
//            //---   Constructor
//            //--------------------------------------------------------------------------------------
//            ViewHolderBase(View itemView) {
//
//                super(itemView);
//
//                txtPortfolioViewTicker = itemView.findViewById(R.id.txtPortfolioViewTicker);
//                txtPortfolioViewCompany = itemView.findViewById(R.id.txtPortfolioViewCompany);
//                txtPortfolioViewPrice = itemView.findViewById(R.id.txtPortfolioViewPrice);
//
////                itemView.setOnCreateContextMenuListener(MainActivity.this);
//
//                itemView.setOnClickListener(this);
//                itemView.setOnLongClickListener(this);
//            }
//
//            //--------------------------------------------------------------------------------------
//            //---   Short Click  ...
//            //--------------------------------------------------------------------------------------
//            @Override
//            public void onClick(View view) {
//
//                if (RecyclerViewItemType.get(getAdapterPosition()) == 3) {
//
//                    RecyclerViewItemType.set(getAdapterPosition(), 4);
//
//                } else {
//
//                    RecyclerViewItemType.set(getAdapterPosition(), 3);
//                }
//
//                adapterStocks.notifyDataSetChanged();
//            }
//
//            //--------------------------------------------------------------------------------------
//            //---   Long CLick ...
//            //--------------------------------------------------------------------------------------
//            @Override
//            public boolean onLongClick(View view) {
//
//                return true;
//            }
//        }
//
//        // convenience method for getting data at click position
//        Stock getItem(int id) {
//
//            return mData.get(id);
//        }
//    }

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
                //---   Category Spinner
                //----------------------------------------------------------------------------------
                Portfolio_Spinner = (Spinner) popupView.findViewById(R.id.spnPortfolioCategory);
                Portfolio_Spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.main_categories, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                Portfolio_Spinner.setAdapter(adapter);
                Portfolio_Spinner.setSelection(Arrays.asList(MainCategoryArray).indexOf(Category));

                //----------------------------------------------------------------------------------
                //---   Subcategory Spinner
                //----------------------------------------------------------------------------------
//                Portfolio_SubCategory_Spinner = (Spinner) popupView.findViewById(R.id.spnPortfolioSubCategory);
//                Portfolio_SubCategory_Spinner.setOnItemSelectedListener(new PortfolioSubCategoryOnItemSelectedListener());
//
//                Portfolio_SubCategory_Adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, SubcategoryArray);
//                Portfolio_SubCategory_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                Portfolio_SubCategory_Spinner.setAdapter(Portfolio_SubCategory_Adapter);
////                Portfolio_SubCategory_Spinner.setSelection(Arrays.asList(CategoryArray).indexOf(Category));

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

//        ExpandableListView.ExpandableListContextMenuInfo info =
//                (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
//
//        CharSequence Title = item.getTitle();
//
//        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
//        int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
//        int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);
//
////        String SubCategory = GroupNames.get(groupPosition);
////        allSubStocks = sqlHandler.getStocksBySubcategory(Category,subCategory);
//
//        i("LOG: (PV) Position", "Group: " + groupPosition + " Child: " + childPosition);
//
//        if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
//            // do something with parent
//
//        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
//            // do someting with child
//
//            String Ticker = subgroupCollection.get(SubcategoryList.get(groupPosition)).get(childPosition);
//
////            Stock currentStock = allStocks.get(childPosition);
//            Stock currentStock = sqlHandler.getStocksByTicker(Ticker);
//
//            switch (Title.toString()) {
//
//                //--------------------------------------------------------------------------------------
//                //---   EDIT
//                //--------------------------------------------------------------------------------------
//                case "Edit":
//
//                    Intent intent2Info = new Intent(PortfolioView.this, StockInfo.class);
//
//                    intent2Info.putExtra("SQL_STOCK_ID", currentStock.getId());
//
//                    //---   ==> CardView
//                    startActivityForResult(intent2Info, REQUEST_INFO);
//
//                    break;
//
//                //----------------------------------------------------------------------------------
//                //---   CLEAR (Copy Price to Base)
//                //----------------------------------------------------------------------------------
//                case "Clear":
//
//                    currentStock.setShares(0);
//                    currentStock.setCommission(0);
//
//                    sqlHandler.updateStock(currentStock);
//
//                    adapterStocks.notifyDataSetChanged();
//
//                    break;
//
//                //----------------------------------------------------------------------------------
//                //---   Tag for Buy
//                //----------------------------------------------------------------------------------
//                case "Tag Buy":
//
//                    currentStock.setWatch(2);
//
//                    sqlHandler.updateStock(currentStock);
//
////                    adapterStocks.notifyDataSetChanged();
//
//                    break;
//
//                //----------------------------------------------------------------------------------
//                //---   Tag for Sell
//                //----------------------------------------------------------------------------------
//                case "Tag Sell":
//
//                    currentStock.setWatch(3);
//
//                    sqlHandler.updateStock(currentStock);
//
//                    adapterStocks.notifyDataSetChanged();
//
//                    break;
//
//                //----------------------------------------------------------------------------------
//                //---   Clear Tag
//                //----------------------------------------------------------------------------------
//                case "Clear Tag":
//
//                    currentStock.setWatch(0);
//
//                    sqlHandler.updateStock(currentStock);
//
////                    adapterStocks.notifyDataSetChanged();
//
//                    break;
//            }
//
//        }

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
        txtCategory.setText("PORTFOLIO");
        txtCategory.setTextColor(ContextCompat.getColor(this, R.color.colorGrey3));

        //------------------------------------------------------------------------------------------
        //---   Touch header -> Category
        //------------------------------------------------------------------------------------------
        txtCategory.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {

                switch (arg1.getAction()) {

                    case MotionEvent.ACTION_DOWN: {

                        Intent intentTraining = new Intent(PortfolioView_old.this, CategoryView.class);
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

            Intent intentYahoo = new Intent(PortfolioView_old.this, NasdaqAPI.class);
            ArrayList<String> TickerList = grabTickers();
            ArrayList<String> TickerPartList = new ArrayList<String>(TickerList.subList(0, TickerList.size()));
            intentYahoo.putStringArrayListExtra("TICKER_INDEX_ARRAY", TickerPartList);
            startActivityForResult(intentYahoo,REQUEST_YAHOO);
        }

        return super.onOptionsItemSelected(item);
    }

    //**********************************************************************************************
    //***   Method: Assign 'type' to each Recycle View row to allow for conditional formatting
    //**********************************************************************************************
    private void prepareRecyclerViewContent() {

        int StateIndex = 0;
        int SizeIndex = 0;
        int MainIndex = 0;
        int SubIndex = 0;

        allStocks = sqlStockHandler.getAllStocks();

        RecyclerViewStockArray.clear();
        RecyclerViewItemType.clear();
        RecyclerViewCategorySize.clear();

        //------------------------------------------------------------------------------------------
        //---   Loop through all Main Categories
        //------------------------------------------------------------------------------------------
        for (String MainCategory: MainCategoryArrayList) {

            //--------------------------------------------------------------------------------------
            //---   Create Dummy Stock as placeholder for 'Main' header
            //--------------------------------------------------------------------------------------
            Stock StockMainHeader = new Stock();
            StockMainHeader.setCategory("MAIN");
//            StockMainHeader.setCompany(Main.toUpperCase().replace("_"," "));
            StockMainHeader.setCompany(MainCategory);

            int MainSize = SubCategoryArrayList.get(MainCategoryArrayList.indexOf(MainCategory)).size();

            RecyclerViewStockArray.add(StockMainHeader);
            RecyclerViewItemType.add(StateIndex++,MAIN_CATEGORY);
            RecyclerViewCategorySize.add(SizeIndex++,MainSize);

            //--------------------------------------------------------------------------------------
            //---   Loop through all Sub Categories
            //--------------------------------------------------------------------------------------
            for (String SubCategory : SubCategoryArrayList.get(MainCategoryArrayList.indexOf(MainCategory))) {

                //----------------------------------------------------------------------------------
                //---   Main Category is expanded ie. show Sub Categories
                //----------------------------------------------------------------------------------
                if (MainCategoryExpand.get(MainIndex)) {

                    //------------------------------------------------------------------------------
                    //---   Create Dummy Stock as placeholder for 'Sub' header
                    //------------------------------------------------------------------------------
                    Stock StockSubHeader = new Stock();
                    StockSubHeader.setCategory("SUB");
                    StockSubHeader.setCompany(SubCategory.replace("_", "&"));

                    ArrayList<Stock> SubStockArray = sqlStockHandler.getStocksByCategory(MainCategory.toUpperCase().replace("_", " "), SubCategory.replace("_", "&"));

                    int SubSize = SubStockArray.size();

                    RecyclerViewStockArray.add(StockSubHeader);
                    RecyclerViewItemType.add(StateIndex++, SUB_CATEGORY);
                    RecyclerViewCategorySize.add(SizeIndex++,SubSize);

                    //------------------------------------------------------------------------------
                    //---   Is this Sub Category expanded or collapsed ie. show Base Categories or skip
                    //------------------------------------------------------------------------------
                    if (SubCategoryExpand.get(SubIndex)) {

                        //--------------------------------------------------------------------------
                        //---   Loop through all Base Items (Stocks)
                        //--------------------------------------------------------------------------
                        for (Stock StockItem : SubStockArray) {

                            RecyclerViewStockArray.add(StockItem);
                            RecyclerViewItemType.add(StateIndex++, STOCK_CATEGORY);
                            RecyclerViewCategorySize.add(SizeIndex++,1);
                        }
                    }
                }

                SubIndex++;
            }

            MainIndex++;
        }
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

//    //**********************************************************************************************
//    //***   onActivityResult (returning from API call)
//    //**********************************************************************************************
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        //--------------------------------------------------------------------------------------
//        //---   Return via finish()
//        //--------------------------------------------------------------------------------------
//        if (resultCode == Activity.RESULT_OK) {
//
//            //------------------------------------------------------------------------------------------
//            //---   Return from Stock Info
//            //------------------------------------------------------------------------------------------
//            if (requestCode == REQUEST_INFO) {
//
//                expListAdapter = new ExpandableListAdapter(PortfolioView.this, SubcategoryList, subgroupCollection);
//                expListView.setAdapter(expListAdapter);
//                expListAdapter.notifyDataSetChanged();
//            }
//
//            //------------------------------------------------------------------------------------------
//            //---   Return from YAHOO
//            //------------------------------------------------------------------------------------------
//            if (requestCode == REQUEST_YAHOO) {
//
//                if (TickerList.size() > 25) {
//
//                    Intent intentYahoo = new Intent(PortfolioView.this, NasdaqAPI.class);
//                    ArrayList<String> TickerPartList = new ArrayList<String>(TickerList.subList(25, TickerList.size()));
//                    intentYahoo.putStringArrayListExtra("TICKER_INDEX_ARRAY", TickerPartList);
//                    startActivityForResult(intentYahoo, REQUEST_YAHOO2);
//
//                } else {
//
//                    expListAdapter = new ExpandableListAdapter(PortfolioView.this, SubcategoryList, subgroupCollection);
//                    expListView.setAdapter(expListAdapter);
//                    expListAdapter.notifyDataSetChanged();
//
//                    expListView.expandGroup(0);
//                }
//            }
//
//            //------------------------------------------------------------------------------------------
//            //---   Return from YAHOO2
//            //------------------------------------------------------------------------------------------
//            if (requestCode == REQUEST_YAHOO2) {
//
//                if (TickerList.size() > 50) {
//
//                    Intent intentYahoo = new Intent(PortfolioView.this, NasdaqAPI.class);
//                    ArrayList<String> TickerPartList = new ArrayList<String>(TickerList.subList(50, TickerList.size()));
//                    intentYahoo.putStringArrayListExtra("TICKER_INDEX_ARRAY", TickerPartList);
//                    startActivityForResult(intentYahoo, REQUEST_YAHOO3);
//
//                } else {
//
//                    expListAdapter = new ExpandableListAdapter(PortfolioView.this, SubcategoryList, subgroupCollection);
//                    expListView.setAdapter(expListAdapter);
//                    expListAdapter.notifyDataSetChanged();
//                }
//            }
//
//            //------------------------------------------------------------------------------------------
//            //---   Return from YAHOO3
//            //------------------------------------------------------------------------------------------
//            if (requestCode == REQUEST_YAHOO3) {
//
//                if (TickerList.size() > 75) {
//
//                    Intent intentYahoo = new Intent(PortfolioView.this, NasdaqAPI.class);
//                    ArrayList<String> TickerPartList = new ArrayList<String>(TickerList.subList(75, TickerList.size()));
//                    intentYahoo.putStringArrayListExtra("TICKER_INDEX_ARRAY", TickerPartList);
//                    startActivityForResult(intentYahoo, REQUEST_YAHOO4);
//
//                } else {
//
//                    expListAdapter = new ExpandableListAdapter(PortfolioView.this, SubcategoryList, subgroupCollection);
//                    expListView.setAdapter(expListAdapter);
//                    expListAdapter.notifyDataSetChanged();
//                }
//
////                expListAdapter = new ExpandableListAdapter(PortfolioView.this, SubcategoryList, subgroupCollection);
////                expListView.setAdapter(expListAdapter);
////                expListAdapter.notifyDataSetChanged();
//            }
//        }
//    }

//    //**********************************************************************************************
//    //***   Method: Refresh Card Layout
//    //**********************************************************************************************
//    private void refreshView(View view) {
//
//        //------------------------------------------------------------------------------------------
//        //---   Update Category Header
//        //------------------------------------------------------------------------------------------
//
////        TextView txtCategory = (TextView) view.findViewById(R.id.txtCategory);
////        txtCategory.setText(Category + " (" + allSubStocks.size() + ")");
//
//        //------------------------------------------------------------------------------------------
//        //---   Layout
//        //------------------------------------------------------------------------------------------
//
//        expListView = (ExpandableListView) view.findViewById(R.id.StockExpandList);
//    }
//
//    //**********************************************************************************************
//    //***   Method: Refresh Card Layout
//    //**********************************************************************************************
//    private void refreshCard () {
//
////        allSubStocks = sqlHandler.getStocksByCategory(Category);
//
//        createSubcategoryList();
//        createSubgroupList();
//
//        expListAdapter = new ExpandableListAdapter(PortfolioView.this, SubcategoryList, subgroupCollection);
//        expListView.setAdapter(expListAdapter);
//        expListAdapter.notifyDataSetChanged();
//    }
}*/
