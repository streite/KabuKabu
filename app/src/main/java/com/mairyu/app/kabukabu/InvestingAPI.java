package com.mairyu.app.kabukabu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

//==================================================================================================
//===   InvestingAPI
//==================================================================================================
public class InvestingAPI extends AppCompatActivity {

    private PreferenceSettings _appPrefs;

    ArrayList<Stock> allStockItems = new ArrayList<>();
    ArrayList<String> TickerList = new ArrayList<>();

    SQLhandler sqlHandler;

//    https://www.investing.com/indices/world-indices

    //**********************************************************************************************
    //***   onCreate
    //**********************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        setContentView(R.layout.google_sheets);

        //------------------------------------------------------------------------------------------
        //---   Preference/Settings
        //------------------------------------------------------------------------------------------

        _appPrefs = new PreferenceSettings(getApplicationContext());

        //------------------------------------------------------------------------------------------
        //---   Get Card Details
        //------------------------------------------------------------------------------------------

        TickerList = getIntent().getStringArrayListExtra("TICKER_INDEX_ARRAY");

        //------------------------------------------------------------------------------------------
        //---   SQLite Setup
        //------------------------------------------------------------------------------------------

        sqlHandler = new SQLhandler(InvestingAPI.this,_appPrefs.getSQLStockDBName(),
                Integer.parseInt(_appPrefs.getSQLStockDBVersion()));

        //------------------------------------------------------------------------------------------
        //---   Initialize credentials and service object
        //------------------------------------------------------------------------------------------

        InvestingAsyncTask investingAsyncTask = new InvestingAsyncTask(InvestingAPI.this);
        investingAsyncTask.execute();
    }

    //**********************************************************************************************
    //***   Pull Data from website, call Parser etc.
    //**********************************************************************************************
    private class InvestingAsyncTask extends AsyncTask <String, Void, ArrayList<Stock>> {

        Context context;

        ProgressDialog progressDialog;

        public InvestingAsyncTask(Context context) {

            this.context = context;
            progressDialog = new ProgressDialog(context);
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: onPreExecute
        //------------------------------------------------------------------------------------------
        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            progressDialog.setTitle("Downloading Info From INVESTING ... Please Wait");
            progressDialog.show();
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: doInBackground
        //------------------------------------------------------------------------------------------
        @Override
        protected ArrayList<Stock> doInBackground(String... params) {

            //--------------------------------------------------------------------------------------
            //---   Setup HTTP client for websites API server
            //--------------------------------------------------------------------------------------

            HTTPClient dictHTTPClient = new HTTPClient();

            dictHTTPClient.setBASE_URL("https://www.investing.com/indices/world-indices");

            //--------------------------------------------------------------------------------------
            //---   Retrieve HTML response from websites (full string)
            //--------------------------------------------------------------------------------------

            String data = (dictHTTPClient.getHTTPData());

            //--------------------------------------------------------------------------------------
            //---   Extract Stock Info via HTML Parser
            //--------------------------------------------------------------------------------------

            try {

                allStockItems = InvestingResponseParser.getTopLoser(data);

                return allStockItems;

            } catch (Throwable t) {

                t.printStackTrace();
            }

            return allStockItems;
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: onPostExecute - Display in ListView
        //------------------------------------------------------------------------------------------
        @Override
        protected void onPostExecute(ArrayList<Stock> allStockItems) {

            super.onPostExecute(allStockItems);

            for (Stock tmpStock: allStockItems) {

                String Ticker = tmpStock.getTicker();
                Stock sqlStock = sqlHandler.getStocksByTicker(Ticker);

                sqlStock.setPrice(tmpStock.getPrice());
                sqlStock.setChange(tmpStock.getChange());
                sqlStock.setPercChange(tmpStock.getChangePerc());

                sqlHandler.updateStock(sqlStock);
            }

            //------------------------------------------------------------------------------------------
            //---   Close progress dialog
            //------------------------------------------------------------------------------------------
            if (progressDialog.isShowing()) {

                progressDialog.dismiss();
            }

            setResult(Activity.RESULT_OK);

            finish();
        }
    }
}
