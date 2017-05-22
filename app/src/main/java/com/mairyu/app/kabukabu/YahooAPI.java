package com.mairyu.app.kabukabu;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

//==================================================================================================
//===   YahooAPI
//==================================================================================================
public class YahooAPI extends AppCompatActivity {

    private YahooAsyncTask yahooAsyncTask;

    TextView txtKanji;

    String Kanji;

    private PreferenceSettings _appPrefs;

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

//        sheetsID = extras.getString("SHEETS_ID");
//        sheetsTab = extras.getString("SHEETS_TAB");
//        sheetsRange = extras.getString("SHEETS_RANGE");
//        sheetsMode = extras.getString("SHEETS_MODE");

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
    private class YahooAsyncTask extends AsyncTask <String, Void, Stock> {

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

            progressDialog.setTitle("Downloading Info From Kanji API ... Please Wait");
            progressDialog.show();
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: doInBackground
        //------------------------------------------------------------------------------------------
        @Override
        protected Stock doInBackground(String... params) {

            Stock stock = new Stock();

            //--------------------------------------------------------------------------------------
            //---   Setup HTTP client for websites API server
            //--------------------------------------------------------------------------------------

            HTTPClient dictHTTPClient = new HTTPClient();

            dictHTTPClient.setBASE_URL("http://query.yahooapis.com/v1/public/yql?"+
                    "q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20%28%22GOOG%22%29"+
                    "&env=store://datatables.org/alltableswithkeys&format=json");
            //--------------------------------------------------------------------------------------
            //---   Retrieve JSON response from websites API (full string)
            //--------------------------------------------------------------------------------------

            String data = (dictHTTPClient.getHTTPData());

            //--------------------------------------------------------------------------------------
            //---   Extract Dictionary Item Array via JSON Parser
            //--------------------------------------------------------------------------------------

            try {

                stock = YahooJSONParser.getYahooStuff(data);

                return stock;

            } catch (Throwable t) {

                t.printStackTrace();

                Log.i("LOG: (DLU) BACKGROUND", "CATCH " + t);
            }

            return stock;
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: onPostExecute - Display in ListView
        //------------------------------------------------------------------------------------------
        @Override
        protected void onPostExecute(Stock stock) {

            super.onPostExecute(stock);

            Stock tmpStock = new Stock();

            tmpStock.setPrice(stock.getPrice());

            sqlHandler.updateStock(tmpStock);

            //------------------------------------------------------------------------------------------
            //---   Close progress dialog
            //------------------------------------------------------------------------------------------
            if (progressDialog.isShowing()) {

                progressDialog.dismiss();
            }
        }
    }
}
