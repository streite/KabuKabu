package com.mairyu.app.kabukabu;

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
        FullSnippet = TatoebaResponse.substring(first,last);
        Snippet = FullSnippet;

        //------------------------------------------------------------------------------------------

        while (FullSnippet.indexOf("onmouseover")>=0) {

            Stock tmpStock = new Stock();

            first = FullSnippet.indexOf("hidelater") + 14;
            last = FullSnippet.indexOf("text-align:left") - 23;
            PartSnippet = Snippet.substring(first, last);
            int Pointer = last;

            first = 0;
            last = PartSnippet.indexOf("<") - 2;
            Ticker = PartSnippet.substring(first,last);
            PartSnippet = PartSnippet.substring(last,PartSnippet.length());

            first = PartSnippet.indexOf("nnum") + 7;
            last = PartSnippet.substring(first,PartSnippet.length()).indexOf("td") - 2 + first;
            Price = PartSnippet.substring(first, last);
            PartSnippet = PartSnippet.substring(last,PartSnippet.length());

            first = PartSnippet.indexOf("nnum") + 6;
            last = PartSnippet.substring(first,PartSnippet.length()).indexOf("td") - 2 + first;
            Change = PartSnippet.substring(first, last);
            PartSnippet = PartSnippet.substring(last,PartSnippet.length());

            first = PartSnippet.indexOf("nnum") + 6;
            last = PartSnippet.substring(first,PartSnippet.length()).indexOf("td") - 2 + first;
            ChangePerc = PartSnippet.substring(first, last);
            PartSnippet = PartSnippet.substring(last,PartSnippet.length());

            first = PartSnippet.indexOf("0px") + 5;
            last = PartSnippet.substring(first,PartSnippet.length()).indexOf("td") - 2 + first;
            Volume = PartSnippet.substring(first, last);
            PartSnippet = PartSnippet.substring(last,PartSnippet.length());

            FullSnippet = Snippet.substring(Pointer,Snippet.length());

            allDictItems.add(tmpStock);

//            if (Snippet.indexOf("onmouseover") > 0) {
//
//                Snippet = Snippet.substring(Snippet.indexOf("sentence-and-translations class=\"sentence-and-translations"));
//            }
        }

        return allDictItems;
    }
}