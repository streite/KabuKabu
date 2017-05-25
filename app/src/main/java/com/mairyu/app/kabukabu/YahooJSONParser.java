package com.mairyu.app.kabukabu;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//==================================================================================================
//===   YahooAPI
//==================================================================================================
public class YahooJSONParser {

    //**********************************************************************************************
    //***
    //**********************************************************************************************

    public static ArrayList<Stock> getYahooStuff(String HTTP_Response) throws JSONException {

        ArrayList<Stock> allStockItems = new ArrayList<>();

        JSONObject yahooJsonObject = new JSONObject(HTTP_Response);

        JSONObject queryJsonObject = yahooJsonObject.getJSONObject("query");

        Log.i("LOG: (DJP) PARSER", "queryJsonObject "+queryJsonObject);

        JSONObject resultsJsonObject = queryJsonObject.getJSONObject("results");

        Log.i("LOG: (DJP) PARSER", "resultsJsonObject "+resultsJsonObject);

        JSONArray quoteJsonArray = resultsJsonObject.getJSONArray("quote");

        Log.i("LOG: (DJP) PARSER", "quoteJsonObject "+quoteJsonArray);

        //--------------------------------------------------------------------------------------
        //---   Loop over all JSON Objects and collect them in ArrayList
        //--------------------------------------------------------------------------------------
        for(int i=0; i < quoteJsonArray.length(); i++) {

            Stock currentStock = new Stock();

            try {
                currentStock.setPrice(Float.parseFloat(getString("LastTradePriceOnly", quoteJsonArray.getJSONObject(i))));
                Log.i("LOG: (DJP) TRY", "LastTradePriceOnly " + getString("LastTradePriceOnly", quoteJsonArray.getJSONObject(i)));

            } catch (Exception e) {

                currentStock.setPrice(0);

                Log.i("LOG: (DJP) CATCH", "LastTradePriceOnly");
            }
        }

        Log.i("LOG: (DJP) TRY", "SIZE "+allStockItems.size());

        if (allStockItems.size() > 0) {

            for (int i = 0; i < allStockItems.size(); i++) {

                Stock currentItem = allStockItems.get(i);

                Log.i("LOG: (DJP) Index---", i+"");
                Log.i("LOG: (DJP) Foreign", currentItem.getPrice()+"");
            }
        }

        return allStockItems;
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