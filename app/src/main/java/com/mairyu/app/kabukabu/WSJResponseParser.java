package com.mairyu.app.kabukabu;

import android.util.Log;

import java.util.ArrayList;


public class WSJResponseParser {

    //**********************************************************************************************
    //***   Parse HTTP response string for sentence pairs
    //**********************************************************************************************
    public static ArrayList<Stock> getTopLoser (String TatoebaResponse) {

        ArrayList<String> ExampleSentences = new ArrayList<>();
        ArrayList<Stock> allDictItems = new ArrayList<>();

        String FullSnippet,PartSnippet,Snippet;
        String Ticker,Price,Change,ChangePerc,Volume;

        int first = TatoebaResponse.indexOf("onmouseover");
        int last = TatoebaResponse.lastIndexOf("Includes common, closed end funds");
//        Log.i("LOG: (WSJP) PARSE", "first ... " + first);
//        Log.i("LOG: (WSJP) PARSE", "last ... " + last);
        FullSnippet = TatoebaResponse.substring(first,last);
        Snippet = FullSnippet;

        //------------------------------------------------------------------------------------------

        while (FullSnippet.indexOf("onmouseover")>=0) {

            Stock tmpStock = new Stock();

            first = FullSnippet.indexOf("hidelater") + 14;
//            last = FullSnippet.indexOf("text-align:left");
            last = FullSnippet.indexOf("</tr>");
            PartSnippet = FullSnippet.substring(first, last);
            int Pointer = last + 1;

            first = 0;
            last = PartSnippet.indexOf("<") - 2;
            Ticker = PartSnippet.substring(first,last);
            tmpStock.setCompany(Ticker.substring(0,Ticker.indexOf("(")-1));
            tmpStock.setTicker(Ticker.substring(Ticker.indexOf("(")+1,Ticker.indexOf(")")));
            PartSnippet = PartSnippet.substring(last,PartSnippet.length());
//            Log.i("LOG: (WSJP) PARSE", "Ticker ... " + Ticker);

            first = PartSnippet.indexOf("nnum") + 6;
            last = PartSnippet.substring(first,PartSnippet.length()).indexOf("td") - 2 + first;
            Price = PartSnippet.substring(first, last);
            Price = Price.replaceAll("\\$","");
            tmpStock.setPrice(Float.parseFloat(Price));
            PartSnippet = PartSnippet.substring(last,PartSnippet.length());

            first = PartSnippet.indexOf("nnum") + 6;
            last = PartSnippet.substring(first,PartSnippet.length()).indexOf("td") - 2 + first;
            Change = PartSnippet.substring(first, last);
            tmpStock.setChange(Change);
            PartSnippet = PartSnippet.substring(last,PartSnippet.length());

            first = PartSnippet.indexOf("nnum") + 6;
            last = PartSnippet.substring(first,PartSnippet.length()).indexOf("td") - 2 + first;
            ChangePerc = PartSnippet.substring(first, last);
            tmpStock.setPercChange(ChangePerc);
            PartSnippet = PartSnippet.substring(last,PartSnippet.length());

            first = PartSnippet.indexOf("0px") + 5;
            last = PartSnippet.substring(first,PartSnippet.length()).indexOf("td") - 2 + first;
            Volume = PartSnippet.substring(first, last);
            tmpStock.setVolume(Volume);
            PartSnippet = PartSnippet.substring(last,PartSnippet.length());

            FullSnippet = FullSnippet.substring(Pointer,FullSnippet.length());
//            Log.i("LOG: (WSJP) PARSE", "FullSnippet ... " + FullSnippet);

            allDictItems.add(tmpStock);

//            if (Snippet.indexOf("onmouseover") > 0) {
//
//                Snippet = Snippet.substring(Snippet.indexOf("sentence-and-translations class=\"sentence-and-translations"));
//            }
        }

        return allDictItems;
    }
}