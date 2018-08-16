package com.mairyu.app.kabukabu;

import android.util.Log;

import com.google.api.services.sheets.v4.model.ValueRange;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//==================================================================================================
//===   HTTPClient
//==================================================================================================
public class HTTPClient {

    private String BASE_URL;

    //**********************************************************************************************
    //***   Establish HTTP Connection with server and read/return data
    //**********************************************************************************************
    public String getHTTPData() {

        HttpURLConnection httpURLConnection = null;

        InputStream inputStream = null;

        Log.i("LOG: (HTTP)", "BASE_URL " + BASE_URL);

        try {

            httpURLConnection = (HttpURLConnection) (new URL(BASE_URL)).openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
//            httpURLConnection.setRequestProperty("X-Mashape-Key", "TqnH5LIxoRmshG1hV4ELviXxsSXap14lqt7jsnPtGbEQBgxcKt");
            httpURLConnection.setRequestProperty("X-Mashape-Key", "fugcGQqIXomshpbhJb4TGsPzuxJap15oYzLjsnWIGOKEmvIkOm");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.connect();
            int fileLength = httpURLConnection.getContentLength();

            StringBuffer stringBuffer = new StringBuffer();
            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;

            Log.i("LOG: (HTTP)", "Progress Start" + fileLength);

            while ((line = bufferedReader.readLine()) != null) {

                stringBuffer.append(line + "\n");

//                Log.i("LOG: (HTTP)", "Progress " + stringBuffer.length());
            }

            Log.i("LOG: (HTTP)", "Progress Done" + stringBuffer.length());

            inputStream.close();

            Log.i("LOG: (HTTP)", "InputStream Closed");

            httpURLConnection.disconnect();

            Log.i("LOG: (HTTP)", "Connection Disconnected");

            return stringBuffer.toString();

        } catch (Throwable t) {

            Log.i("LOG: (HTTP) - CATCH", "Error " + t);

            t.printStackTrace();

            return null;

        } finally {

            try {

                inputStream.close();

                httpURLConnection.disconnect();

            } catch (Throwable t) {

                t.printStackTrace();
            }
        }
    }

    //**********************************************************************************************
    //***   Establish HTTP Connection with server and write data
    //**********************************************************************************************
    public String setHTTPData() {

        HttpURLConnection httpURLConnection = null;

        Log.i("LOG: (HTTP) - POST", "BASE_URL " + BASE_URL);

        try {

            URL url = new URL(BASE_URL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            httpURLConnection.setInstanceFollowRedirects(false);
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setRequestProperty("charset", "utf-8");
            httpURLConnection.setUseCaches (false);

            httpURLConnection.connect();

            Log.i("LOG: (HTTP) - POST", "CONNECTED");

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream ());

            Log.i("LOG: (HTTP) - POST", "WR "+httpURLConnection);

            //--------------------
            String range = "WRITE!A1:B1"; //Read the docs on how these ranges work.
            //for the values that you want to input, create a list of object lists
            List<List<Object>> values = new ArrayList<>();
            //Where each value represents the list of objects that is to be written to a range
            //I simply want to edit a single row, so I use a single list of objects
            List<Object> data1 = new ArrayList<>();
            data1.add("objA");
            data1.add("objB");
            //There are obviously more dynamic ways to do these, but you get the picture
            values.add(data1);
            //Create the valuerange object and set its fields
            ValueRange valueRange = new ValueRange();
            valueRange.setMajorDimension("ROWS");
            valueRange.setRange(range);
            valueRange.setValues(values);

            //---------------------------------------------

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("range", "WRITE!A1:B1");
            jsonParam.put("majorDimension", "ROWS");
            jsonParam.put("values", "Donald,Trump");  // {"Email":"aaa@tbbb.com","Password":"123456"}

            wr.writeBytes(jsonParam.toString());

            Log.i("LOG: (HTTP) - POST", "WRITTEN" + jsonParam);

            wr.flush();

            Log.i("LOG: (HTTP) - POST", "FLUSHED");

            wr.close();

        } catch (Throwable t) {

            Log.i("LOG: (HTTP) - CATCH", "Error " + t);

            t.printStackTrace();
        }

        return "YO";
    }

    //**********************************************************************************************
    //***   Set URL
    //**********************************************************************************************
    public void setBASE_URL(String BASE_URL) {

        this.BASE_URL = BASE_URL;
    }
}
