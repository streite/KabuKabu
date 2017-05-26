package com.mairyu.app.kabukabu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

//==================================================================================================
//===   YahooAPI
//==================================================================================================
public class YahooAPI extends AppCompatActivity {

    private YahooAsyncTask yahooAsyncTask;

    TextView txtKanji;

    String Kanji;

    private PreferenceSettings _appPrefs;

    ArrayList<Stock> allStockItems = new ArrayList<>();
    ArrayList<String> TickerList = new ArrayList<>();

    SQLhandler sqlHandler;

    //**********************************************************************************************
    //***   onCreate
    //**********************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_sheets);

        //------------------------------------------------------------------------------------------
        //---   Preference/Settings
        //------------------------------------------------------------------------------------------

        _appPrefs = new PreferenceSettings(getApplicationContext());

        //------------------------------------------------------------------------------------------
        //---   Get Card Details
        //------------------------------------------------------------------------------------------

        Bundle extras = getIntent().getExtras();

        TickerList = getIntent().getStringArrayListExtra("TICKER_INDEX_ARRAY");

        //------------------------------------------------------------------------------------------
        //---   Preference/Settings
        //------------------------------------------------------------------------------------------

        _appPrefs = new PreferenceSettings(getApplicationContext());

        //------------------------------------------------------------------------------------------
        //---   SQLite Setup
        //------------------------------------------------------------------------------------------

        sqlHandler = new SQLhandler(YahooAPI.this,_appPrefs.getSQLDBName(),Integer.parseInt(_appPrefs.getSQLDBVersion()));

        //------------------------------------------------------------------------------------------
        //---   Initialize credentials and service object
        //------------------------------------------------------------------------------------------

        YahooAsyncTask yahooAsyncTask = new YahooAsyncTask(YahooAPI.this);
        yahooAsyncTask.execute();
    }

    //**********************************************************************************************
    //***   Pull Data from website, call Parser etc.
    //**********************************************************************************************
    private class YahooAsyncTask extends AsyncTask <String, Void, ArrayList<Stock>> {

        Context context;

        ProgressDialog progressDialog;

        public YahooAsyncTask (Context context) {

            this.context = context;
            progressDialog = new ProgressDialog(context);
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: onPreExecute
        //------------------------------------------------------------------------------------------
        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            progressDialog.setTitle("Downloading Info From YAHOO API ... Please Wait");
            progressDialog.show();
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: doInBackground
        //------------------------------------------------------------------------------------------
        @Override
        protected ArrayList<Stock> doInBackground(String... params) {

            Stock stock = new Stock();

            //--------------------------------------------------------------------------------------
            //---   Setup HTTP client for websites API server
            //--------------------------------------------------------------------------------------

            HTTPClient dictHTTPClient = new HTTPClient();

            String TickerConcat = "";

            for(String tmp: TickerList) {

                TickerConcat = TickerConcat + tmp + ",";
            }
            TickerConcat.replaceAll(",$","");

            dictHTTPClient.setBASE_URL("http://query.yahooapis.com/v1/public/yql?"+
                    "q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20%28%22"+
                    TickerConcat+
                    "%22%29&env=store://datatables.org/alltableswithkeys&format=json");

            //--------------------------------------------------------------------------------------
            //---   Retrieve JSON response from websites API (full string)
            //--------------------------------------------------------------------------------------

            String data = (dictHTTPClient.getHTTPData());

            //--------------------------------------------------------------------------------------
            //---   Extract Dictionary Item Array via JSON Parser
            //--------------------------------------------------------------------------------------

            try {

                allStockItems = YahooJSONParser.getYahooStuff(data);

                return allStockItems;

            } catch (Throwable t) {

                t.printStackTrace();

                Log.i("LOG: (DLU) BACKGROUND", "CATCH " + t);
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

                sqlHandler.updateStock(tmpStock);
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
