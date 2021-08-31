package com.mairyu.app.kabukabu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

//==================================================================================================
//===   NasdaqAPI
//==================================================================================================
public class NasdaqAPI extends AppCompatActivity {

    private SharedPreferences _appPrefs;

    ArrayList<Stock> allStockItems = new ArrayList<>();
    ArrayList<String> TickerList = new ArrayList<>();

    SQLStockHandler sqlHandler;

    // Quandle API Key:  -zz6UJA1CAmN7KGPa_2L
    // Alpha: YAP5UEWWNGV6AZE6

    // https://www.etrade.wallst.com/v1/stocks/multisnapshot/multisnapshot.asp?symbols=NVDA&peer=false

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

        _appPrefs = new SharedPreferences(getApplicationContext());

        //------------------------------------------------------------------------------------------
        //---   Get Card Details
        //------------------------------------------------------------------------------------------

        TickerList = getIntent().getStringArrayListExtra("TICKER_INDEX_ARRAY");

        //------------------------------------------------------------------------------------------
        //---   SQLite Setup
        //------------------------------------------------------------------------------------------

        sqlHandler = new SQLStockHandler(NasdaqAPI.this,_appPrefs.getSQLStockDBName(),
                Integer.parseInt(_appPrefs.getSQLStockDBVersion()));

        //------------------------------------------------------------------------------------------
        //---   Initialize credentials and service object
        //------------------------------------------------------------------------------------------

        NasdaqAsyncTask nasdaqAsyncTask = new NasdaqAsyncTask(NasdaqAPI.this);
        nasdaqAsyncTask.execute();
    }

    //**********************************************************************************************
    //***   Pull Data from website, call Parser etc.
    //**********************************************************************************************
    private class NasdaqAsyncTask extends AsyncTask <String, Void, ArrayList<Stock>> {

        Context context;

        ProgressDialog progressDialog;

        public NasdaqAsyncTask(Context context) {

            this.context = context;
            progressDialog = new ProgressDialog(context);
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: onPreExecute
        //------------------------------------------------------------------------------------------
        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            progressDialog.setTitle("Downloading Info From NASDAQ ... Please Wait");
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

            String TickerConcat = "";

            for(String tmp: TickerList) {

                TickerConcat = TickerConcat + tmp + ",";
            }

            TickerConcat = TickerConcat.replaceAll(",$","");

            dictHTTPClient.setBASE_URL("https://www.nasdaq.com/aspx/flashquotes.aspx?symbol=" + TickerConcat);

            //--------------------------------------------------------------------------------------
            //---   Retrieve HTML response from websites (full string)
            //--------------------------------------------------------------------------------------

            String data = (dictHTTPClient.getHTTPData());

            //--------------------------------------------------------------------------------------
            //---   Extract Stock Info via HTML Parser
            //--------------------------------------------------------------------------------------

            try {

                allStockItems = NasdaqResponseParser.getTopLoser(data);

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
                sqlStock.setVolume(tmpStock.getVolume());

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
