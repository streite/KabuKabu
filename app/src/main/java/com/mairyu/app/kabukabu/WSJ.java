package com.mairyu.app.kabukabu;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

//==================================================================================================
//===   WSJ
//==================================================================================================
public class WSJ extends Activity {

    TextView txtHTML;

//    TextView mTextView;

    String fullResponse;

    //**********************************************************************************************
    //***   onCreate
    //**********************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_info);

        //------------------------------------------------------------------------------------------
        //---   Mimic Popup Window
        //------------------------------------------------------------------------------------------

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.95),(int)(height*0.7));

        //--------------------------------------------------------------------------------------
        //---   Setup HTTP client for websites API server
        //--------------------------------------------------------------------------------------

        TatoebaAsyncStuff tatoebaAsyncStuff = new TatoebaAsyncStuff(WSJ.this);

        tatoebaAsyncStuff.execute();
    }

    //**********************************************************************************************
    //***   Pull Data from website, call Parser etc.
    //**********************************************************************************************
    private class TatoebaAsyncStuff extends AsyncTask<String, Void, String> {

        Context context;

//        ProgressDialog progressDialog;

        public TatoebaAsyncStuff(Context context) {

            this.context = context;
//            progressDialog = new ProgressDialog(context);
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: onPreExecute
        //------------------------------------------------------------------------------------------
        @Override
        protected void onPreExecute() {

            super.onPreExecute();

//            progressDialog.setTitle("Downloading Info From Tatoeba Web Page ... Please Wait");
//            progressDialog.show();
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: doInBackground
        //------------------------------------------------------------------------------------------
        @Override
        protected String doInBackground(String... params) {

            InputStream stream = null;
            HttpURLConnection httpURLConnection = null;
            String result = null;
            String url = "http://online.wsj.com/mdc/public/page/2_3021-losecomp-loser.html";
//            String url = "https://tatoeba.org/eng/sentences/search?query=rain&from=eng&to=jpn";

            try {
                httpURLConnection = (HttpURLConnection) (new URL(url)).openConnection();

            } catch (IOException io) {

                Log.i("LOG: (WJS) CATCH", "httpURLConnection ... " + httpURLConnection);
            }

            try {

                httpURLConnection.setRequestMethod("GET");

            } catch (ProtocolException pe) {

            }

            //----------------------------------------------------------------------------------
            //---   open a communications link to the resource referenced by the URL
            //----------------------------------------------------------------------------------
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

            return result;
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: onPostExecute - Display in ListView
        //------------------------------------------------------------------------------------------
        @Override
        protected void onPostExecute(String HTML_Source) {

            super.onPostExecute(HTML_Source);

            ArrayList<Stock> ExampleSentences = new ArrayList<>();

            Log.i("LOG: (KV) POST", "HTML "+HTML_Source);

            ExampleSentences = WSJResponseParser.getTopLoser(HTML_Source);

//            mTextView.setText(ExampleSentences.get(0)+ExampleSentences.get(1));


//            if (progressDialog.isShowing()) {
//
//                progressDialog.dismiss();
//            }
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