package com.mairyu.app.kabukabu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

//==================================================================================================
//===   InvestingAPI
//==================================================================================================
public class InvestingAPI extends AppCompatActivity {

    private PreferenceSettings _appPrefs;

    ArrayList<Index> allIndexItems = new ArrayList<>();

    HashMap<String, String> IndexChangeLUT = new HashMap<>();

    SQLStockHandler sqlHandler;

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

        sqlHandler = new SQLStockHandler(InvestingAPI.this,_appPrefs.getSQLStockDBName(),
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
    private class InvestingAsyncTask extends AsyncTask <String, Void, ArrayList<Index>> {

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
        protected ArrayList<Index> doInBackground(String... params) {

            //--------------------------------------------------------------------------------------
            //---   Setup HTTP client for websites API server
            //--------------------------------------------------------------------------------------

            HTTPClient dictHTTPClient = new HTTPClient();

            dictHTTPClient.setBASE_URL("https://www.investing.com/indices/world-indices");

            //--------------------------------------------------------------------------------------
            //---   Retrieve HTML response from websites (full string)
            //--------------------------------------------------------------------------------------

            String data = (dictHTTPClient.getHTTPData());

            Log.i("LOG: (IA)", "Data Received");

            //--------------------------------------------------------------------------------------
            //---   Extract Stock Info via HTML Parser
            //--------------------------------------------------------------------------------------

            try {

                allIndexItems = WorldIndexResponseParser.getWorldIndexArray(data);

                Log.i("LOG: (IA)", "Parse Done");

                return allIndexItems;

            } catch (Throwable t) {

                t.printStackTrace();
            }

            return allIndexItems;
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: onPostExecute - Display in ListView
        //------------------------------------------------------------------------------------------
        @Override
        protected void onPostExecute(ArrayList<Index> IndexChangeArray) {

            super.onPostExecute(IndexChangeArray);

            //------------------------------------------------------------------------------------------
            //---   Close progress dialog
            //------------------------------------------------------------------------------------------
            if (progressDialog.isShowing()) {

                progressDialog.dismiss();
            }

//            intent.putExtra("h", hashMap);

            Intent intentIndex = new Intent(getApplicationContext(), IndexView.class);

//            intentIndex.putExtra("HASH_MAP", IndexChangeLUT);
            intentIndex.putParcelableArrayListExtra("INDEX_CHANGE_ARRAY", IndexChangeArray);

            setResult(Activity.RESULT_OK,intentIndex);

            finish();
        }
    }
}
