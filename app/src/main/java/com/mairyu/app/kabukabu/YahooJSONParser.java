package com.mairyu.app.kabukabu;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

//==================================================================================================
//===   YahooAPI
//==================================================================================================
public class YahooJSONParser {

    //**********************************************************************************************
    //***
    //**********************************************************************************************

    public static Stock getYahooStuff(String HTTP_Response) throws JSONException {

        JSONObject yahooJsonObject = new JSONObject(HTTP_Response);

        JSONObject queryJsonObject = yahooJsonObject.getJSONObject("query");

        Log.i("LOG: (DJP) PARSER", "queryJsonObject "+queryJsonObject);

        JSONObject resultsJsonObject = queryJsonObject.getJSONObject("results");

        Log.i("LOG: (DJP) PARSER", "resultsJsonObject "+resultsJsonObject);

        JSONObject quoteJsonObject = resultsJsonObject.getJSONObject("quote");

        Log.i("LOG: (DJP) PARSER", "quoteJsonObject "+quoteJsonObject);

        Stock currentStock = new Stock();

        try {
            currentStock.setPrice(Float.parseFloat(getString("LastTradePriceOnly", quoteJsonObject)));
            Log.i("LOG: (DJP) TRY", "LastTradePriceOnly " + getString("LastTradePriceOnly", quoteJsonObject));

        } catch (Exception e) {

            currentStock.setPrice(0);

            Log.i("LOG: (DJP) CATCH", "LastTradePriceOnly");
        }

        return currentStock;
    }

    //**********************************************************************************************
    //***   Get single JSON Object
    //**********************************************************************************************
    private static JSONObject getJsonObject (String tagName, JSONObject jsonObject) throws JSONException {

        return jsonObject.getJSONObject(tagName);
    }

    //**********************************************************************************************
    //***   Get String from JSON Object based on Tag
    //**********************************************************************************************
    private static String getString (String tagName, JSONObject jsonObject) throws JSONException {

//        Log.i("LOG: (DLU) PARSER", "STRING "+jsonObject.getString(tagName));

        return jsonObject.getString(tagName);
    }

    //**********************************************************************************************
    //***
    //**********************************************************************************************
    private static int getInt (String tagName, JSONObject jsonObject) throws JSONException {

        return (int) jsonObject.getInt(tagName);
    }

    //**********************************************************************************************
    //***
    //**********************************************************************************************
    private static float getFloat (String tagName, JSONObject jsonObject) throws JSONException {

        return (float) jsonObject.getDouble(tagName);
    }

    //**********************************************************************************************
    //***
    //**********************************************************************************************
    private static boolean getBoolean (String tagName, JSONObject jsonObject) throws JSONException {

        return (boolean) jsonObject.getBoolean(tagName);
    }
}