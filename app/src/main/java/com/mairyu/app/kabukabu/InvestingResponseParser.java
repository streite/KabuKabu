package com.mairyu.app.kabukabu;

import android.util.Log;

import java.util.HashMap;


public class InvestingResponseParser {

    //**********************************************************************************************
    //***   Parse HTTP response string for sentence pairs
    //**********************************************************************************************
    public static HashMap<String, String> getIndexHashMap (String InvestingResponse) {

        HashMap<String, String> IndexChangeLUT = new HashMap<>();

        String FullSnippet,PartSnippet,tmpSnippet;
        String Country,Index,Change;

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

                PartSnippet = skipOnce(PartSnippet);
                first = PartSnippet.indexOf("span title=") + 12;
                last = PartSnippet.indexOf(" class=\"ceFlags") - 1;
                Country = PartSnippet.substring(first, last);

//                Log.i("IRP: Country", Country);

                PartSnippet = skipOnce(PartSnippet);
                first = PartSnippet.indexOf("title=") + 6;
                PartSnippet = PartSnippet.substring(first);
                first = PartSnippet.indexOf(">") + 1;
                last = PartSnippet.indexOf("<") - 0;
                Index = PartSnippet.substring(first, last);

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

//                Log.i("IRP: Change", Change);

                IndexChangeLUT.put(Index, Change);

                PartSnippet = PartSnippet.substring(PartSnippet.indexOf("<tr id=") + 1);
            }

//            FullSnippet = FullSnippet.substring(FullSnippet.indexOf("</script><table id")+1);
//            FullSnippet = FullSnippet.substring(FullSnippet.indexOf("</script><table id")+0);

//            Log.i("IRP: FullSnippet", FullSnippet);
//        }

        return IndexChangeLUT;
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