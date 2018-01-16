package com.mairyu.app.kabukabu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

//==================================================================================================
//===   EarningsView
//==================================================================================================
public class EarningsView extends AppCompatActivity {

    private ArrayAdapter<String> adapterCategory;

    private ExpandableListAdapter expListAdapter;
    private ExpandableListView expListView;

    private String[] CategoryArray;
    private String[] SubcategoryArray;

    Map<String, Earning> EarningsMap = new HashMap<>();

    ArrayList<String> EarningsDateList = new ArrayList<>();
    ArrayList<String> childList = new ArrayList<>();
    Map<String, ArrayList<String>> EarningsDateCollection;

    ArrayList<String> TickerList = new ArrayList<>();
    ArrayList<String> CompanyList = new ArrayList<>();
    ArrayList<String> TimeList = new ArrayList<>();
    ArrayList<String> EPSList = new ArrayList<>();

    DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

    Calendar calendarNext;

    static final int REQUEST_EARNINGS = 3000;
    static final int REQUEST_EARNINGS2 = 3001;
    static final int REQUEST_EARNINGS3 = 3002;
    static final int REQUEST_EARNINGS4 = 3003;
    static final int REQUEST_EARNINGS5 = 3004;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.earnings_view);

        EarningsDateCollection = new LinkedHashMap<String, ArrayList<String>>();

        //------------------------------------------------------------------------------------------
        //---   ListView Setup
        //------------------------------------------------------------------------------------------

//        CategoryArray = getResources().getStringArray(R.array.categories);
//        adapterCategory = new CustomDatabaseAdapter();
//
//        categoryListView = (ListView) findViewById(R.id.categoryListView);
//        categoryListView.setAdapter(adapterCategory);

        expListView = (ExpandableListView) findViewById(R.id.EarningsExpandList);
        expListAdapter = new ExpandableListAdapter(EarningsView.this, EarningsDateList, EarningsDateCollection);
        expListView.setAdapter(expListAdapter);

        //------------------------------------------------------------------------------------------
        //---   Find first valid future Earnings Day
        //------------------------------------------------------------------------------------------

//        Calendar calendarNow = Calendar.getInstance();

        calendarNext = getNextEarningsDate(Calendar.getInstance());

        int dayOfWeek = calendarNext.get(Calendar.DAY_OF_WEEK);
        Date rightNow = calendarNext.getTime();

        Intent intentEarnings = new Intent(EarningsView.this, EarningsAPI.class);
        intentEarnings.putExtra("DATE", df.format(rightNow));
        intentEarnings.putExtra("WEEKDAY", dayOfWeek);
        EarningsDateList.add(df.format(rightNow));
        startActivityForResult(intentEarnings, REQUEST_EARNINGS);
    }

    //**********************************************************************************************
    //***   onActivityResult (returning from API call)
    //**********************************************************************************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        //--------------------------------------------------------------------------------------
        //---   Return via finish()
        //--------------------------------------------------------------------------------------
        if (resultCode == Activity.RESULT_OK) {

            //--------------------------------------------------------------------------------------
            //---   Return from Earnings
            //--------------------------------------------------------------------------------------
            if (requestCode == REQUEST_EARNINGS) {

                TickerList = intent.getStringArrayListExtra("TICKER_ARRAY");
                CompanyList = intent.getStringArrayListExtra("COMPANY_ARRAY");
                TimeList = intent.getStringArrayListExtra("TIME_ARRAY");
                EPSList = intent.getStringArrayListExtra("EPS_ARRAY");
                String Date = intent.getExtras().getString("DATE");

                createSubgroupList(Date,TickerList);

                calendarNext = getNextEarningsDate(calendarNext);

                int dayOfWeek = calendarNext.get(Calendar.DAY_OF_WEEK);
                Date rightNow = calendarNext.getTime();

                Intent intentEarnings = new Intent(EarningsView.this, EarningsAPI.class);
                intentEarnings.putExtra("DATE", df.format(rightNow));
                intentEarnings.putExtra("WEEKDAY", dayOfWeek);
                EarningsDateList.add(df.format(rightNow));

                startActivityForResult(intentEarnings, REQUEST_EARNINGS2);
            }

            //--------------------------------------------------------------------------------------
            //---   Return from Earnings
            //--------------------------------------------------------------------------------------
            if (requestCode == REQUEST_EARNINGS2) {

                TickerList = intent.getStringArrayListExtra("TICKER_ARRAY");
                CompanyList = intent.getStringArrayListExtra("COMPANY_ARRAY");
                TimeList = intent.getStringArrayListExtra("TIME_ARRAY");
                EPSList = intent.getStringArrayListExtra("EPS_ARRAY");
                String Date = intent.getExtras().getString("DATE");

//                createSubcategoryList();
                createSubgroupList(Date,TickerList);

                expListAdapter = new ExpandableListAdapter(EarningsView.this, EarningsDateList, EarningsDateCollection);
                expListView.setAdapter(expListAdapter);
                expListAdapter.notifyDataSetChanged();
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
        menu_refresh.setVisible(false);

        return true;
    }

    //**********************************************************************************************
    //***   onOptionsItemSelected (Toolbar)
    //**********************************************************************************************
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    //**********************************************************************************************
    //***   Custom Expandable Adapter for PortfolioPage
    //**********************************************************************************************

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Activity context;
        private Map<String, ArrayList<String>> laptopCollections;
        private ArrayList<String> GroupNames;

        //------------------------------------------------------------------------------------------
        //---   Constructor
        //------------------------------------------------------------------------------------------
        public ExpandableListAdapter(Activity context, ArrayList<String> GroupNames,
                                     Map<String, ArrayList<String>> laptopCollections) {

            this.context = context;
            this.laptopCollections = laptopCollections;
            this.GroupNames = GroupNames;
        }

        //------------------------------------------------------------------------------------------
        //---   Group is expanded
        //------------------------------------------------------------------------------------------
//        @Override
//        public void onGroupExpanded(int groupPosition){
//
//            super.onGroupExpanded(groupPosition);
////            lastExpandedGroupPosition = groupPosition;
//        }

        //------------------------------------------------------------------------------------------
        //---
        //------------------------------------------------------------------------------------------
        public Object getChild(int groupPosition, int childPosition) {

            return laptopCollections.get(GroupNames.get(groupPosition)).get(childPosition);
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        //------------------------------------------------------------------------------------------
        //---   Child View (stock Info)
        //------------------------------------------------------------------------------------------
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
                                 View childView, ViewGroup parent) {

            LayoutInflater inflater = context.getLayoutInflater();

            if (childView == null) {
                childView = inflater.inflate(R.layout.earnings_child_item, null);
            }

            RelativeLayout earnings_child_item_view = (RelativeLayout) childView.findViewById(R.id.earnings_child_item_view);
            earnings_child_item_view.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.gradient_light_grey_bg, null));

            final String Ticker = (String) getChild(groupPosition, childPosition);

            TextView itemView = (TextView) childView.findViewById(R.id.EarningsListViewTicker);
            itemView.setText(Ticker);

            TextView nameView = (TextView) childView.findViewById(R.id.EarningsListViewName);
            nameView.setText(Ticker);

            return childView;
        }

        public int getChildrenCount(int groupPosition) {

            return laptopCollections.get(GroupNames.get(groupPosition)).size();
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
                groupView = infalInflater.inflate(R.layout.group_item, null);
            }

            RelativeLayout rlhDataBase = (RelativeLayout) groupView.findViewById(R.id.group_item_view);
            rlhDataBase.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.gradient_dark_grey_bg, null));

            TextView GroupHeader = (TextView) groupView.findViewById(R.id.portfolioListViewGroupName);

            //--------------------------------------------------------------------------------------
            //---   Update Category Header
            //--------------------------------------------------------------------------------------

//            GroupName = "11/24/2017 (Monday)";

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

    //------------------------------------------------------------------------------------------
    //---   Create collection of Earning Dates
    //------------------------------------------------------------------------------------------
    private void createSubgroupList(String earningsDate, ArrayList<String> tickerList) {

            childList = new ArrayList<String>();

            String[] tmpList = new String[tickerList.size()];

            for (int i = 0; i < tickerList.size(); i++) {

                tmpList[i] = tickerList.get(i);
            }

            for (String model : tmpList)
                childList.add(model);

            EarningsDateCollection.put(earningsDate, childList);


//        for (String earningsDate : EarningsDateList) {
//
//            childList = new ArrayList<String>();
//
//            String[] tmpList = new String[TickerList.size()];
//
//            for (int i = 0; i < TickerList.size(); i++) {
//
//                tmpList[i] = TickerList.get(i);
//            }
//
//            for (String model : tmpList)
//                childList.add(model);
//
//            EarningsDateCollection.put(earningsDate, childList);
//        }
    }

    //------------------------------------------------------------------------------------------
    //---   Skip holidays and get next working day
    //------------------------------------------------------------------------------------------
    private Calendar getNextEarningsDate(Calendar currentCalendar) {

        int dayOfWeek = currentCalendar.get(Calendar.DAY_OF_WEEK);

        switch (dayOfWeek) {

            case Calendar.FRIDAY:
                currentCalendar.add(Calendar.DAY_OF_YEAR, 3);
                break;

            case Calendar.SATURDAY:
                currentCalendar.add(Calendar.DAY_OF_YEAR, 2);
                break;

            default:
                currentCalendar.add(Calendar.DAY_OF_YEAR, 1);
                break;
        }

        return (currentCalendar);
    }
}