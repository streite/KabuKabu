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

//    private static String BASE_URL = "https://itunes.apple.com/search?term="+"Def+Leppard";
//    private String BASE_URL = "http://jisho.org/api/v1/search/words?keyword="+"father";
//    http://api.pearson.com/v2/dictionaries/ldec/entries?headword=car&limit=4
//    http://docs.microsofttranslator.com/oauth-token.html#!/Authentication_token_service/getToken
//    http://docs.microsofttranslator.com/text-translate.html#/
//    ID1: 2d8bcc8111b24b8e846b22eae45d3fa3 ID2: 1e99ef6c39ed48288b04c457f5fefdad

//    http://www.setgetgo.com/randomword/get.php (random Word)
//    https://en.wikipedia.org/w/api.php?format=json&action=query&generator=random&grnnamespace=0&prop=revisions|images&rvprop=content&grnlimit=10
//    http://api.wordnik.com/v4/words.json/randomWords?hasDictionaryDef=true&minCorpusCount=0&minLength=5&maxLength=15&limit=1&api_key=a2a73e7b926c924fad7001ca3111acd55af2ffabf50eb4ae5

    // Test Mashape API Key: TqnH5LIxoRmshG1hV4ELviXxsSXap14lqt7jsnPtGbEQBgxcKt
    // Production Mashape API Key: fugcGQqIXomshpbhJb4TGsPzuxJap15oYzLjsnWIGOKEmvIkOm

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

            StringBuffer stringBuffer = new StringBuffer();
            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {

                stringBuffer.append(line + "\n");
            }

            inputStream.close();

            httpURLConnection.disconnect();

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
