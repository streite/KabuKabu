package com.mairyu.app.kabukabu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

//==================================================================================================
//===   PortfolioView
//==================================================================================================
public class PortfolioView extends AppCompatActivity {

    private ArrayList<Stock> allStocks = new ArrayList<>();
    private ArrayAdapter<Stock> adapterStocks;

    private ListView listViewAllStocks;

    private String[] CategoryArray;

    private PreferenceSettings _appPrefs;

    SQLhandler sqlHandler;
    String SQL_Filename;
    int SQLDBSize;

    //**********************************************************************************************
    //***   onCreate
    //**********************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.portfolio_view);

        //------------------------------------------------------------------------------------------
        //---   Preference/Settings
        //------------------------------------------------------------------------------------------

        _appPrefs = new PreferenceSettings(getApplicationContext());

        //------------------------------------------------------------------------------------------
        //---   ListView Setup
        //------------------------------------------------------------------------------------------

        listViewAllStocks = (ListView) findViewById(R.id.listViewAllStocks);

        registerForContextMenu(listViewAllStocks);

        //------------------------------------------------------------------------------------------
        //---   SQLite Setup
        //------------------------------------------------------------------------------------------

        sqlHandler = new SQLhandler(PortfolioView.this,_appPrefs.getSQLDBName(),Integer.parseInt(_appPrefs.getSQLDBVersion()));

        //------------------------------------------------------------------------------------------
        //---   If cards are already loaded, show right away (wait until activity is stable)
        //------------------------------------------------------------------------------------------

        findViewById(R.id.llv_portfolio_view).post(new Runnable() {

            public void run() {

                allStocks = sqlHandler.getAllStocks(false);

                adapterStocks = new CustomDatabaseAdapter();
                listViewAllStocks.setAdapter(adapterStocks);

                SQLDBSize = sqlHandler.getStockCount();
            }
        });
    }

    //**********************************************************************************************
    //***   Custom Array Adapter for DatabasePage
    //**********************************************************************************************
    private class CustomDatabaseAdapter extends ArrayAdapter<Stock> {

        public CustomDatabaseAdapter() {

            super(PortfolioView.this, R.layout.portfolio_list_view_item,allStocks);
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

            return itemView;
        }
    }
}
