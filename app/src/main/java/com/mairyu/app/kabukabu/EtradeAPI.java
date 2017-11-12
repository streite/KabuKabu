package com.mairyu.app.kabukabu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

//==================================================================================================
//===   YahooAPI
//==================================================================================================
public class EtradeAPI extends AppCompatActivity {

    private EtradeAsyncTask etradeAsyncTask;

    TextView txtKanji;

    String Kanji;

    private PreferenceSettings _appPrefs;

    ArrayList<Stock> allStockItems = new ArrayList<>();
    ArrayList<String> TickerList = new ArrayList<>();

    SQLhandler sqlHandler;

    // Quandle API Key:  -zz6UJA1CAmN7KGPa_2L
    // Alpha: YAP5UEWWNGV6AZE6

    // https://www.etrade.wallst.com/v1/stocks/multisnapshot/multisnapshot.asp?symbols=NVDA&peer=false

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

        sqlHandler = new SQLhandler(EtradeAPI.this,_appPrefs.getSQLStockDBName(),
                Integer.parseInt(_appPrefs.getSQLStockDBVersion()));

        //------------------------------------------------------------------------------------------
        //---   Initialize credentials and service object
        //------------------------------------------------------------------------------------------

        EtradeAsyncTask etradeAsyncTask = new EtradeAsyncTask(EtradeAPI.this);
        etradeAsyncTask.execute();
    }

    //**********************************************************************************************
    //***   Pull Data from website, call Parser etc.
    //**********************************************************************************************
    private class EtradeAsyncTask extends AsyncTask <String, Void, ArrayList<Stock>> {

        Context context;

        ProgressDialog progressDialog;

        public EtradeAsyncTask(Context context) {

            this.context = context;
            progressDialog = new ProgressDialog(context);
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: onPreExecute
        //------------------------------------------------------------------------------------------
        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            progressDialog.setTitle("Downloading Info From ETRADE ... Please Wait");
            progressDialog.show();
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: doInBackground
        //------------------------------------------------------------------------------------------
        @Override
        protected ArrayList<Stock> doInBackground(String... params) {

/*
            InputStream stream = null;
            HttpsURLConnection httpURLConnection = null;
            String result = null;
//            String url = "https://finance.yahoo.com/quote/NVDA";
//            String url = "https://www.etrade.wallst.com/v1/stocks/multisnapshot/multisnapshot.asp?symbols=NVDA&peer=false";
            String url = "http://www.nasdaq.com/aspx/flashquotes.aspx?symbol=NVDA,AMD";

            try {
                httpURLConnection = (HttpsURLConnection) (new URL(url)).openConnection();

            } catch (IOException io) {

            }

            try {

                // Timeout for reading InputStream arbitrarily set to 3000ms.
                httpURLConnection.setReadTimeout(3000);
                // Timeout for connection.connect() arbitrarily set to 3000ms.
                httpURLConnection.setConnectTimeout(3000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("X-Mashape-Key", "TqnH5LIxoRmshG1hV4ELviXxsSXap14lqt7jsnPtGbEQBgxcKt");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");

                httpURLConnection.setRequestProperty("Content-Type", "text/html; charset=UTF-8");
                httpURLConnection.setRequestProperty("Connection", "keep-alive");
                httpURLConnection.setRequestProperty("Strict-Transport-Security", "max-age=86400; preload");
//                httpURLConnection.setRequestProperty("Content-Length" , "139934");
                httpURLConnection.setRequestProperty("Vary", "Accept-Encoding");
                httpURLConnection.setRequestProperty("Accept-Encoding", "identity");

                httpURLConnection.setDoInput(true);

            } catch (ProtocolException pe) {

            }

            //----------------------------------------------------------------------------------
            //---   open a communications link to the resource referenced by the URL
            //----------------------------------------------------------------------------------
            try {

                httpURLConnection.connect();

            } catch (SocketTimeoutException ste) {

                Log.i("LOG: (KV) CATCH", "connect-SocketTimeoutException ... " + ste);

            } catch (IOException io) {

                Log.i("LOG: (KV) CATCH", "connect-IOException ... " + io);
            }

            //----------------------------------------------------------------------------------
            //---   get the status code from an HTTP response message
            //----------------------------------------------------------------------------------
            try {
                int responseCode = httpURLConnection.getResponseCode();

                Log.i("LOG: (KV) TRY", "responseCode ... " + responseCode);

            } catch (IOException io) {

                Log.i("LOG: (KV) CATCH", "responseCode-IOException ... " + io);
            }
            // Retrieve the response body as an InputStream.
            //----------------------------------------------------------------------------------
            //---   return an input stream that reads from this open connection
            //----------------------------------------------------------------------------------
            try {
                stream = httpURLConnection.getInputStream();
            } catch (IOException io) {

//                } catch (UnknownServiceException use) {
            }

            try {

                if (stream != null) {
                    // Converts Stream to String with max length of 500.
                    result = readStream(stream, 500000);
                }
            } catch (IOException io) {

            }

            allStockItems = EtradeResponseParser.getTopLoser(result);

            Log.i("LOG: (KV) TRY", "stream ..."+stream);
            Log.i("LOG: (KV) TRY", "result ..."+result);

            // Close Stream and disconnect HTTPS connection.
            try {
                if (stream != null) {
                    stream.close();
                }
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            } catch (IOException i) {

            }
*/























            //--------------------------------------------------------------------------------------
            //---   Setup HTTP client for websites API server
            //--------------------------------------------------------------------------------------

            HTTPClient dictHTTPClient = new HTTPClient();

            String TickerConcat = "";

            for(String tmp: TickerList) {

                TickerConcat = TickerConcat + tmp + ",";
            }

            TickerConcat = TickerConcat.replaceAll(",$","");

//            String symbol="NVDA";
//            String url='https://finance.yahoo.com/quote/' + symbol;
//            resp = requests.get(url)

//            dictHTTPClient.setBASE_URL("http://finance.yahoo.com/quote/" + TickerConcat);
//            dictHTTPClient.setBASE_URL("https://www.etrade.wallst.com/v1/stocks/multisnapshot/multisnapshot.asp?symbols=NVDA,AMD&peer=false");
            dictHTTPClient.setBASE_URL("http://www.nasdaq.com/aspx/flashquotes.aspx?symbol=" + TickerConcat);

            //--------------------------------------------------------------------------------------
            //---   Retrieve JSON response from websites API (full string)
            //--------------------------------------------------------------------------------------

            String data = (dictHTTPClient.getHTTPData());

            //--------------------------------------------------------------------------------------
            //---   Extract Dictionary Item Array via JSON Parser
            //--------------------------------------------------------------------------------------

            try {

                allStockItems = EtradeResponseParser.getTopLoser(data);

                return allStockItems;

            } catch (Throwable t) {

                t.printStackTrace();

                Log.i("LOG: (YAH) BACKGROUND", "CATCH " + t);
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

    private String readStream(InputStream stream, int maxLength) throws IOException {
        String result = null;
        // Read InputStream using the UTF-8 charset.
        InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
        // Create temporary buffer to hold Stream data with specified max length.
        char[] buffer = new char[maxLength];
        // Populate temporary buffer with Stream data.
        int numChars = 0;
        int readSize = 0;
        while (numChars < maxLength && readSize != -1) {
            numChars += readSize;
            int pct = (100 * numChars) / maxLength;
            readSize = reader.read(buffer, numChars, buffer.length - numChars);
        }
        if (numChars != -1) {
            // The stream was not empty.
            // Create String that is actual length of response body if actual length was less than
            // max length.
            numChars = Math.min(numChars, maxLength);
            result = new String(buffer, 0, numChars);
        }
        return result;
    }

}
