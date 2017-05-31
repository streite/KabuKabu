package com.mairyu.app.kabukabu;

import java.util.ArrayList;


public class WSJResponseParser {

    //**********************************************************************************************
    //***   Parse HTTP response string for sentence pairs
    //**********************************************************************************************
    public static ArrayList<Stock> getTopLoser (String TatoebaResponse) {

        ArrayList<String> ExampleSentences = new ArrayList<>();
        ArrayList<Stock> allDictItems = new ArrayList<>();

        String Snippet,Ticker,Price,Change,ChangePerc,Volume;

        int first = TatoebaResponse.indexOf("onmouseover");
        int last = TatoebaResponse.lastIndexOf("Includes common, closed end funds");
        Snippet = TatoebaResponse.substring(first,last);

        //------------------------------------------------------------------------------------------

        while (Snippet.indexOf("onmouseover")>=0) {

            Stock dictionaryItem = new Stock();

            int length = Snippet.length();

            first = Snippet.indexOf("hidelater") + 14;
            last = Snippet.indexOf("text-align:left") - 30;
            Snippet = Snippet.substring(first, last);
            Ticker = Snippet.substring(0,Snippet.indexOf("<") - 2);
            Snippet = Snippet.substring(Snippet.indexOf("<") - 2, last);
            Snippet = Snippet.substring(last);

            allDictItems.add(dictionaryItem);

            if (Snippet.indexOf("onmouseover") > 0) {

                    Snippet = Snippet.substring(Snippet.indexOf("sentence-and-translations class=\"sentence-and-translations"));
            }
        }

        return allDictItems;
    }
}