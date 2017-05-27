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

//        HashMap<String,Float> TickerPriceHash = new HashMap<String,Float>();

        ArrayList<Stock> allStockItems = new ArrayList<>();

        JSONObject yahooJsonObject = new JSONObject(HTTP_Response);

        JSONObject queryJsonObject = yahooJsonObject.getJSONObject("query");

        Log.i("LOG: (YJP) PARSER", "queryJsonObject "+queryJsonObject);

        int JSONCount = Integer.parseInt(getString("count", queryJsonObject));

        Log.i("LOG: (YJP) PARSER", "JSONCount "+JSONCount);

        JSONObject resultsJsonObject = queryJsonObject.getJSONObject("results");

        Log.i("LOG: (YJP) PARSER", "resultsJsonObject "+resultsJsonObject);

        if (JSONCount > 1) {

            JSONArray quoteJsonArray = resultsJsonObject.getJSONArray("quote");

            Log.i("LOG: (YJP) PARSER", "quoteJsonObject " + quoteJsonArray);

            //--------------------------------------------------------------------------------------
            //---   Loop over all JSON Objects and collect them in ArrayList
            //--------------------------------------------------------------------------------------
            for (int i = 0; i < quoteJsonArray.length(); i++) {

                Stock currentStock = new Stock();

                currentStock.setTicker(getString("symbol", quoteJsonArray.getJSONObject(i)));
                currentStock.setPrice(Float.parseFloat(getString("LastTradePriceOnly", quoteJsonArray.getJSONObject(i))));
                currentStock.setChange(getString("Change", quoteJsonArray.getJSONObject(i)));
                currentStock.setPercChange(getString("PercentChange", quoteJsonArray.getJSONObject(i)));

                allStockItems.add(currentStock);
            }

        } else {

            JSONObject quoteJsonObject = resultsJsonObject.getJSONObject("quote");

            Log.i("LOG: (YJP) PARSER", "quoteJsonObject " + quoteJsonObject);

            Stock currentStock = new Stock();

            currentStock.setTicker(getString("symbol", quoteJsonObject));
            currentStock.setPrice(Float.parseFloat(getString("LastTradePriceOnly", quoteJsonObject)));
            currentStock.setChange(getString("Change", quoteJsonObject));
            currentStock.setPercChange(getString("PercentChange", quoteJsonObject));

            allStockItems.add(currentStock);
        }

        Log.i("LOG: (YJP) TRY", "SIZE "+allStockItems.size());

        if (allStockItems.size() > 0) {

            for (int i = 0; i < allStockItems.size(); i++) {

                Stock currentItem = allStockItems.get(i);

                Log.i("LOG: (YJP) Index---", i+"");
                Log.i("LOG: (YJP) Foreign", currentItem.getPrice()+"");
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