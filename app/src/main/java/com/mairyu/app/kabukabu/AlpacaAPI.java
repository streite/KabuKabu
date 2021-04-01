package com.mairyu.app.kabukabu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

//==================================================================================================
//===   AlpacaAPI
//==================================================================================================
public class AlpacaAPI extends AppCompatActivity {

    private PreferenceSettings _appPrefs;

    ArrayList<Stock> allStockItems = new ArrayList<>();
    ArrayList<String> TickerList = new ArrayList<>();

    SQLhandler sqlHandler;

    private String ALPACA_KEY = "PKAMLB3LMN9DZ2L549I3";
    private String ALPACA_SECRET_KEY = "K81Odtpfbb0McmyJ56dKinHbJxjXNtk1/yf/VUA0";

    // Quandle API Key:  -zz6UJA1CAmN7KGPa_2L
    // Alpha: YAP5UEWWNGV6AZE6

    // www.google.com/finance/option_chain?q=AAPL&output=json

//    // Create watchlist
//try {
//        Watchlist dayTradeWatchlist = alpacaAPI.createWatchlist("Day Trade", "AAPL");
//
//        System.out.println("\n\nDay Trade Watchlist:");
//        System.out.println("\t" + dayTradeWatchlist.toString().replace(",", ",\n\t"));
//    } catch (AlpacaAPIRequestException e) {
//        e.printStackTrace();
//    }
//
//// Get bars
//try {
//        ZonedDateTime start = ZonedDateTime.of(2019, 11, 18, 0, 0, 0, 0, ZoneId.of("America/New_York"));
//        ZonedDateTime end = ZonedDateTime.of(2019, 11, 22, 23, 59, 0, 0, ZoneId.of("America/New_York"));
//
//        Map<String, ArrayList<Bar>> bars = alpacaAPI.getBars(BarsTimeFrame.ONE_DAY, "AAPL", null, start, end,
//                null, null);
//
//        System.out.println("\n\nBars response:");
//        for (Bar bar : bars.get("AAPL")) {
//            System.out.println("\t==========");
//            System.out.println("\tUnix Time " + ZonedDateTime.ofInstant(Instant.ofEpochSecond(bar.getT()),
//                    ZoneOffset.UTC));
//            System.out.println("\tOpen: $" + bar.getO());
//            System.out.println("\tHigh: $" + bar.getH());
//            System.out.println("\tLow: $" + bar.getL());
//            System.out.println("\tClose: $" + bar.getC());
//            System.out.println("\tVolume: " + bar.getV());
//        }
//    } catch (AlpacaAPIRequestException e) {
//        e.printStackTrace();
//    }

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

        sqlHandler = new SQLhandler(AlpacaAPI.this,_appPrefs.getSQLStockDBName(),
                Integer.parseInt(_appPrefs.getSQLStockDBVersion()));

        //------------------------------------------------------------------------------------------
        //---   Initialize credentials and service object
        //------------------------------------------------------------------------------------------

        AlpacaAsyncTask alpacaAsyncTask = new AlpacaAsyncTask(AlpacaAPI.this);
        alpacaAsyncTask.execute();
    }

    //**********************************************************************************************
    //***   Pull Data from website, call Parser etc.
    //**********************************************************************************************
    private class AlpacaAsyncTask extends AsyncTask <String, Void, ArrayList<Stock>> {

        Context context;

        ProgressDialog progressDialog;

        public AlpacaAsyncTask(Context context) {

            this.context = context;
            progressDialog = new ProgressDialog(context);
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: onPreExecute
        //------------------------------------------------------------------------------------------
        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            progressDialog.setTitle("Downloading Info From ALPACA API ... Please Wait");
            progressDialog.show();
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: doInBackground
        //------------------------------------------------------------------------------------------
        @Override
        protected ArrayList<Stock> doInBackground(String... params) {

            Stock stock = new Stock();

            InputStream stream = null;
            HttpsURLConnection httpURLConnection = null;
            String result = null;
            String url = "https://data.alpaca.markets:443/v1/bars/1Min?symbols=SDOW,DXD,NVDA&start=2019-05-02T09:20:00-08&limit=1";

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
                httpURLConnection.setRequestProperty("APCA-API-KEY-ID", ALPACA_KEY);
                httpURLConnection.setRequestProperty("APCA-API-SECRET-KEY", ALPACA_SECRET_KEY);
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

            //--------------------------------------------------------------------------------------
            //---   open a communications link to the resource referenced by the URL
            //--------------------------------------------------------------------------------------
            try {

                httpURLConnection.connect();

                Log.i("LOG: (KV) TRY", "httpURLConnection ... " + httpURLConnection);

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

//
//
//
//
//
//            //--------------------------------------------------------------------------------------
//            //---   Setup HTTP client for websites API server
//            //--------------------------------------------------------------------------------------
//
//            HTTPClient dictHTTPClient = new HTTPClient();
//
//            String TickerConcat = "";
//
//            for(String tmp: TickerList) {
//
//                TickerConcat = TickerConcat + tmp + ",";
//            }
//            TickerConcat = TickerConcat.replaceAll(",$","");
//            TickerConcat = "NVDA";
//
////            String symbol="NVDA";
////            String url='https://finance.yahoo.com/quote/' + symbol;
////            resp = requests.get(url)
//
//            dictHTTPClient.setBASE_URL("http://finance.yahoo.com/quote/" + TickerConcat);
//
//            //--------------------------------------------------------------------------------------
//            //---   Retrieve JSON response from websites API (full string)
//            //--------------------------------------------------------------------------------------
//
//            String data = (dictHTTPClient.getHTTPData());
//
            //--------------------------------------------------------------------------------------
            //---   Extract Dictionary Item Array via JSON Parser
            //--------------------------------------------------------------------------------------

            try {

                allStockItems = AlpacaJSONParser.getAlpacaStuff(result);

                return allStockItems;

            } catch (Throwable t) {

                t.printStackTrace();

                Log.i("LOG: (ALP) BACKGROUND", "CATCH " + t);
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
