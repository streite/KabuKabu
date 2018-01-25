package com.mairyu.app.kabukabu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;

//==================================================================================================
//===   InvestingAPI
//==================================================================================================
public class InvestingAPI extends AppCompatActivity {

    private PreferenceSettings _appPrefs;

    HashMap<String, String> IndexChangeLUT = new HashMap<>();

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

//        TickerList = getIntent().getStringArrayListExtra("TICKER_INDEX_ARRAY");

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
    private class InvestingAsyncTask extends AsyncTask <String, Void, HashMap<String, String>> {

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
        protected HashMap<String, String> doInBackground(String... params) {

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

                IndexChangeLUT = InvestingResponseParser.getIndexHashMap(data);

                return IndexChangeLUT;

            } catch (Throwable t) {

                t.printStackTrace();
            }

            return IndexChangeLUT;
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: onPostExecute - Display in ListView
        //------------------------------------------------------------------------------------------
        @Override
        protected void onPostExecute(HashMap<String, String> IndexChangeLUT) {

            super.onPostExecute(IndexChangeLUT);

            //------------------------------------------------------------------------------------------
            //---   Close progress dialog
            //------------------------------------------------------------------------------------------
            if (progressDialog.isShowing()) {

                progressDialog.dismiss();
            }

//            intent.putExtra("h", hashMap);

            Intent intentIndex = new Intent(getApplicationContext(), IndexView.class);

            intentIndex.putExtra("HASH_MAP", IndexChangeLUT);

            setResult(Activity.RESULT_OK,intentIndex);

            finish();
        }
    }
}
