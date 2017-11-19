package com.mairyu.app.kabukabu;

import java.util.ArrayList;


public class EtradeResponseParser {

    //**********************************************************************************************
    //***   Parse HTTP response string for sentence pairs
    //**********************************************************************************************
    public static ArrayList<Stock> getTopLoser (String TatoebaResponse) {

        ArrayList<String> ExampleSentences = new ArrayList<>();
        ArrayList<Stock> allDictItems = new ArrayList<>();

        String FullSnippet,PartSnippet,Color;
        String Ticker,Price,Change,ChangePerc,Volume;

        int first = TatoebaResponse.indexOf("quotes_content_left_flashdata");
        int last = TatoebaResponse.lastIndexOf("Updates every 7 Seconds");
        FullSnippet = TatoebaResponse.substring(first,last);
        first = FullSnippet.indexOf("flashevengr");
        last = FullSnippet.lastIndexOf("table") + 8;
        FullSnippet = FullSnippet.substring(first,last);

        //------------------------------------------------------------------------------------------

        while (FullSnippet.indexOf("flashevengr")>=0) {

            Stock tmpStock = new Stock();

            first = FullSnippet.indexOf("symbol");
            last = FullSnippet.indexOf("table");
            PartSnippet = FullSnippet.substring(first, last);

            first = PartSnippet.indexOf(">") + 1;
            last = PartSnippet.indexOf("<") - 0;
            Ticker = PartSnippet.substring(first,last);
            tmpStock.setTicker(Ticker);
            PartSnippet = PartSnippet.substring(last,PartSnippet.length());

            first = PartSnippet.indexOf("color:") + 6;
            last = PartSnippet.substring(first,PartSnippet.length()).indexOf("id=") - 2 + first;
            Color = PartSnippet.substring(first, last);

            first = PartSnippet.indexOf("lastsale") + 11;
            last = PartSnippet.substring(first,PartSnippet.length()).indexOf("label") - 2 + first;
            Price = PartSnippet.substring(first, last);
            Price = Price.replace(",","");
            tmpStock.setPrice(Float.parseFloat(Price));
            PartSnippet = PartSnippet.substring(last,PartSnippet.length());

            first = PartSnippet.indexOf("change") + 8;
            last = PartSnippet.substring(first,PartSnippet.length()).indexOf("label") - 8 + first;
            Change = PartSnippet.substring(first, last);
            if (Change.equals("unch")) { Change = "0"; }
            if (Color.equals("Red")) { Change = "-" + Change; }
            else { Change = "+" + Change; }
            tmpStock.setChange(Change);
            PartSnippet = PartSnippet.substring(last,PartSnippet.length());

            first = PartSnippet.indexOf("pctchange") + 11;
            last = PartSnippet.substring(first,PartSnippet.length()).indexOf("label") - 2 + first;
            ChangePerc = PartSnippet.substring(first, last);
            if (ChangePerc.equals("unch")) { ChangePerc = "0"; }
            if (Color.equals("Red")) { ChangePerc = "-" + ChangePerc; }
            else { ChangePerc = "+" + ChangePerc; }
            tmpStock.setPercChange(ChangePerc);
            PartSnippet = PartSnippet.substring(last,PartSnippet.length());

            first = PartSnippet.indexOf("volume") + 8;
            last = PartSnippet.substring(first,PartSnippet.length()).indexOf("label") - 2 + first;
            Volume = PartSnippet.substring(first, last);
            tmpStock.setVolume(Volume);

            FullSnippet = FullSnippet.substring(FullSnippet.indexOf("symbol")+1);

            allDictItems.add(tmpStock);

//            if (Snippet.indexOf("flashevengr") > 0) {
//
//                Snippet = Snippet.substring(Snippet.indexOf("flashevengr"));
//            }
        }

        return allDictItems;
    }
}