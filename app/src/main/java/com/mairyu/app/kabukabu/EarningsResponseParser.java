package com.mairyu.app.kabukabu;

import java.util.ArrayList;


public class EarningsResponseParser {

    //**********************************************************************************************
    //***   Parse HTTP response string for sentence pairs
    //**********************************************************************************************
    public static ArrayList<Earning> getEarningsInfo(String EarningsResponse) {

        ArrayList<Earning> allEarningItems = new ArrayList<>();

        String FullSnippet,PartSnippet;
        String Ticker,Time;

        int first = EarningsResponse.indexOf("<tbody>");
        int last = EarningsResponse.indexOf("</tbody>");
        FullSnippet = EarningsResponse.substring(first,last);

        //------------------------------------------------------------------------------------------

        while (FullSnippet.indexOf("<tr>")>=0) {

            Earning tmpEarning = new Earning();

            first = FullSnippet.indexOf("<tr>");
            last = FullSnippet.indexOf("</tr>");
            PartSnippet = FullSnippet.substring(first, last);

            first = PartSnippet.indexOf("symbols=") + 8;
            last = PartSnippet.indexOf("title") - 2;
            Ticker = PartSnippet.substring(first,last);
            tmpEarning.setTicker(Ticker);
            PartSnippet = PartSnippet.substring(PartSnippet.indexOf("</td>")+2,PartSnippet.length());

            first = PartSnippet.indexOf("grey-text") + 11;
            last = PartSnippet.indexOf("</td>") - 0;
            Time = PartSnippet.substring(first,last);
            Time = Time.replaceAll("\t","");
            Time = Time.replaceAll("\n","");
            tmpEarning.setTime(Time);
            PartSnippet = PartSnippet.substring(PartSnippet.indexOf("</td>")+2,PartSnippet.length());

            FullSnippet = FullSnippet.substring(FullSnippet.indexOf("</tr>")+1);

            allEarningItems.add(tmpEarning);
        }

        return allEarningItems;
    }
}