package com.mairyu.app.kabukabu;

import android.util.Log;

import java.util.ArrayList;


public class WorldIndexResponseParser {

    //**********************************************************************************************
    //***   Parse HTTP response string for sentence pairs
    //**********************************************************************************************
    public static ArrayList<Index> getWorldIndexArray (String InvestingResponse) {

//        HashMap<String, String> IndexChangeLUT = new HashMap<>();

        ArrayList<Index> allIndexItems = new ArrayList<>();

        String FullSnippet,PartSnippet,tmpSnippet;
        String Country,Index,Change,OpenClose;

        int first = InvestingResponse.indexOf("</script><table id");
        int last = InvestingResponse.lastIndexOf("plusLoader.ready");
        FullSnippet = InvestingResponse.substring(first,last);

        //------------------------------------------------------------------------------------------
        //---   One Country at a time ...
        //------------------------------------------------------------------------------------------
//        while (FullSnippet.indexOf("</script><table id")>=0) {

//            Log.i("IRP: Full", FullSnippet.substring(0,50));

            first = FullSnippet.indexOf("<tr id=");
            PartSnippet = FullSnippet.substring(first);

//            first = PartSnippet.indexOf("<tr id=");
//            last = PartSnippet.indexOf("</td></tr>");
//            tmpSnippet = FullSnippet.substring(first, last);

            while (PartSnippet.indexOf("<tr id=")>=0) {

//                Log.i("IRP: Part", PartSnippet.substring(0,50));

                Index tmpIndex = new Index();

                PartSnippet = skipOnce(PartSnippet);
                first = PartSnippet.indexOf("span title=") + 12;
                last = PartSnippet.indexOf(" class=\"ceFlags") - 1;
                Country = PartSnippet.substring(first, last);
                tmpIndex.setCountry(Country);

                Log.i("IRP: Country", Country);

                PartSnippet = skipOnce(PartSnippet);
                first = PartSnippet.indexOf("title=") + 6;
                PartSnippet = PartSnippet.substring(first);
                first = PartSnippet.indexOf(">") + 1;
                last = PartSnippet.indexOf("<") - 0;
                Index = PartSnippet.substring(first, last);
                tmpIndex.setName(Index);

                Log.i("IRP: Index", Index);

                PartSnippet = skipOnce(PartSnippet);
                PartSnippet = skipOnce(PartSnippet);
                PartSnippet = skipOnce(PartSnippet);
                PartSnippet = skipOnce(PartSnippet);
                PartSnippet = skipOnce(PartSnippet);
                first = PartSnippet.indexOf(">") + 1;
                last = PartSnippet.indexOf("</");
                Change = PartSnippet.substring(first, last);
                Change = Change.replace("%", "");
                Change = Change.replace("+-", "");
                tmpIndex.setPercChange(Float.parseFloat(Change));

                PartSnippet = skipOnce(PartSnippet);
                PartSnippet = skipOnce(PartSnippet);
                first = PartSnippet.indexOf("<span class=") + 13;
                last = PartSnippet.indexOf(" isOpenExch");
                OpenClose = PartSnippet.substring(first, last);
                if (OpenClose.equals("redClockIcon")) {
                    tmpIndex.setMarketOpen(0);
                } else {
                    tmpIndex.setMarketOpen(1);
                }

                allIndexItems.add(tmpIndex);

                PartSnippet = PartSnippet.substring(PartSnippet.indexOf("<tr id=") + 1);
            }

        return allIndexItems;
    }

    //**********************************************************************************************
    //***   Method: Empty SQL DB (for clean slate)
    //**********************************************************************************************
    public static String skipOnce(String ParseLine){

        int index;

        index = ParseLine.indexOf("<td class")+1;
        ParseLine = ParseLine.substring(index);

        return ParseLine;
    }
}