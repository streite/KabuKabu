package com.mairyu.app.kabukabu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

//==================================================================================================
//===   EarningsView
//==================================================================================================
public class EarningsView extends AppCompatActivity {

    private ArrayAdapter<String> adapterCategory;

    private List<String> CategoryMenu = new ArrayList<String>();

    private ListView categoryListView;

    private String[] CategoryArray;

    ArrayList<String> TickerList = new ArrayList<>();;
    ArrayList<String> TimeList = new ArrayList<>();;
    ArrayList<String> EPSList = new ArrayList<>();;

    static final int REQUEST_EARNINGS = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.earnings_view);

        //------------------------------------------------------------------------------------------
        //---   ListView Setup
        //------------------------------------------------------------------------------------------

//        CategoryArray = getResources().getStringArray(R.array.categories);
//        adapterCategory = new CustomDatabaseAdapter();
//
//        categoryListView = (ListView) findViewById(R.id.categoryListView);
//        categoryListView.setAdapter(adapterCategory);

        //------------------------------------------------------------------------------------------
        //---   Listen for Click (ListView)
        //------------------------------------------------------------------------------------------

        Intent intentEarnings = new Intent(EarningsView.this, EarningsAPI.class);
        intentEarnings.putExtra("DATE", "foo");
        startActivityForResult(intentEarnings,REQUEST_EARNINGS);
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
                TimeList = intent.getStringArrayListExtra("TIME_ARRAY");
                EPSList = intent.getStringArrayListExtra("EPS_ARRAY");
            }
        }
    }

    //**********************************************************************************************
    //***   Custom Array Adapter for DatabasePage
    //**********************************************************************************************
    private class CustomDatabaseAdapter extends ArrayAdapter<String> {

        public CustomDatabaseAdapter() {

            super(EarningsView.this, R.layout.category_list_view_item,CategoryArray);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View itemView = convertView;

            if (itemView == null) {

                itemView = getLayoutInflater().inflate(R.layout.category_list_view_item, parent, false);
            }

            // Find the current card to work with
            String currentString = CategoryArray[position];

            TextView menuOption = (TextView) itemView.findViewById(R.id.txtStockGroup);
            menuOption.setText(currentString);

            return itemView;
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
}
