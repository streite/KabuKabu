package com.mairyu.app.kabukabu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

//==================================================================================================
//===   EarningsAPI
//==================================================================================================
public class EarningsAPI extends AppCompatActivity {

    private PreferenceSettings _appPrefs;

    ArrayList<Earning> allEarningItems = new ArrayList<>();

    ArrayList<String> TickerList = new ArrayList<String>();;
    ArrayList<String> CompanyList = new ArrayList<String>();;
    ArrayList<String> TimeList = new ArrayList<String>();;
    ArrayList<String> EPSList = new ArrayList<String>();;

    SQLhandler sqlHandler;

    String EarningDate;

    // https://finance.yahoo.com/calendar/earnings?&day=2017-11-14
    // http://www.nasdaq.com/earnings/earnings-calendar.aspx?date=2017-Nov-14
    // https://eresearch.fidelity.com/eresearch/conferenceCalls.jhtml?tab=earnings&begindate=11/14/2017

    //**********************************************************************************************
    //***   onCreate
    //**********************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        setContentView(R.layout.earnings_view);

        //------------------------------------------------------------------------------------------
        //---   Preference/Settings
        //------------------------------------------------------------------------------------------

        _appPrefs = new PreferenceSettings(getApplicationContext());

        //------------------------------------------------------------------------------------------
        //---   Get Card Details
        //------------------------------------------------------------------------------------------

//        TickerList = getIntent().getStringArrayListExtra("DATE");

        Bundle extras = getIntent().getExtras();
        EarningDate = extras.getString("DATE");

        //------------------------------------------------------------------------------------------
        //---   SQLite Setup
        //------------------------------------------------------------------------------------------

        sqlHandler = new SQLhandler(EarningsAPI.this,_appPrefs.getSQLStockDBName(),
                Integer.parseInt(_appPrefs.getSQLStockDBVersion()));

        //------------------------------------------------------------------------------------------
        //---   Initialize credentials and service object
        //------------------------------------------------------------------------------------------

        EarningsAsyncTask earningsAsyncTask = new EarningsAsyncTask(EarningsAPI.this);
        earningsAsyncTask.execute();
    }

    //**********************************************************************************************
    //***   Pull Data from website, call Parser etc.
    //**********************************************************************************************
    private class EarningsAsyncTask extends AsyncTask <String, Void, ArrayList<Earning>> {

        Context context;

        ProgressDialog progressDialog;

        public EarningsAsyncTask(Context context) {

            this.context = context;
            progressDialog = new ProgressDialog(context);
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: onPreExecute
        //------------------------------------------------------------------------------------------
        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            progressDialog.setTitle("Downloading Info From Earnings ... Please Wait");
            progressDialog.show();
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: doInBackground
        //------------------------------------------------------------------------------------------
        @Override
        protected ArrayList<Earning> doInBackground(String... params) {

            //--------------------------------------------------------------------------------------
            //---   Setup HTTP client for websites API server
            //--------------------------------------------------------------------------------------

            HTTPClient dictHTTPClient = new HTTPClient();

            String URL = "https://eresearch.fidelity.com/eresearch/conferenceCalls.jhtml?tab=earnings&begindate=" + EarningDate;

            dictHTTPClient.setBASE_URL(URL);

            //--------------------------------------------------------------------------------------
            //---   Retrieve JSON response from websites (full string)
            //--------------------------------------------------------------------------------------

            String data = (dictHTTPClient.getHTTPData());

            //--------------------------------------------------------------------------------------
            //---   Extract Earning Info via HTML Parser
            //--------------------------------------------------------------------------------------

            try {

                allEarningItems = EarningsResponseParser.getEarningsInfo(data);

                return allEarningItems;

            } catch (Throwable t) {

                t.printStackTrace();
            }

            return allEarningItems;
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: onPostExecute - Display in ListView
        //------------------------------------------------------------------------------------------
        @Override
        protected void onPostExecute(ArrayList<Earning> allEarningItems) {

            super.onPostExecute(allEarningItems);

            //------------------------------------------------------------------------------------------
            //---   Compile Array List of all Companies for this particular Date
            //------------------------------------------------------------------------------------------
            for (Earning tmpEarning: allEarningItems) {

                TickerList.add(tmpEarning.getTicker());
                CompanyList.add(tmpEarning.getCompany());
                TimeList.add(tmpEarning.getTime());
                EPSList.add(tmpEarning.getEPS());
            }

            //------------------------------------------------------------------------------------------
            //---   Close progress dialog
            //------------------------------------------------------------------------------------------
            if (progressDialog.isShowing()) {

                progressDialog.dismiss();
            }

            Intent intent2Earning = new Intent(EarningsAPI.this, EarningsView.class);
            intent2Earning.putStringArrayListExtra("TICKER_ARRAY", TickerList);
            intent2Earning.putStringArrayListExtra("COMPANY_ARRAY", CompanyList);
            intent2Earning.putStringArrayListExtra("TIME_ARRAY", TimeList);
            intent2Earning.putStringArrayListExtra("EPS_ARRAY", EPSList);
            intent2Earning.putExtra("DATE", EarningDate);

            setResult(Activity.RESULT_OK,intent2Earning);

            finish();
        }
    }
}
