package com.mairyu.app.kabukabu;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;


public class InvestingResponseParser {

    //**********************************************************************************************
    //***   Parse HTTP response string for sentence pairs
    //**********************************************************************************************
    public static Map<String, String> getIndexHashMap (String InvestingResponse) {

        Map<String, String> IndexChangeLUT = new HashMap<>();

        String FullSnippet,PartSnippet,tmpSnippet;
        String Country,Index,Change;

        int first = InvestingResponse.indexOf("</script><table id");
        int last = InvestingResponse.lastIndexOf("plusLoader.ready");
        FullSnippet = InvestingResponse.substring(first,last);

        //------------------------------------------------------------------------------------------
        //---   One Country at a time ...
        //------------------------------------------------------------------------------------------
        while (FullSnippet.indexOf("</script><table id")>=0) {

//            Stock tmpStock = new Stock();

            first = FullSnippet.indexOf("<tr id=");
            last = FullSnippet.indexOf("</td></tr>");
            PartSnippet = FullSnippet.substring(first, last);

            tmpSnippet = skipOnce(PartSnippet);
            first = tmpSnippet.indexOf("span title=") + 12;
            last = tmpSnippet.indexOf(" class=\"ceFlags") - 1;
            Country = tmpSnippet.substring(first, last);

            Log.i("IRP: Country", Country);

            tmpSnippet = skipOnce(tmpSnippet);
            first = tmpSnippet.indexOf("span data-name=") + 16;
            last = tmpSnippet.indexOf("data-id") - 2;
            Index = tmpSnippet.substring(first, last);

            Log.i("IRP: Index", Index);

            tmpSnippet = skipOnce(tmpSnippet);
            tmpSnippet = skipOnce(tmpSnippet);
            tmpSnippet = skipOnce(tmpSnippet);
            tmpSnippet = skipOnce(tmpSnippet);
            tmpSnippet = skipOnce(tmpSnippet);
            first = tmpSnippet.indexOf(">") + 1;
            last = tmpSnippet.indexOf("</");
            Change = tmpSnippet.substring(first, last);

            Log.i("IRP: Change", Change);

            IndexChangeLUT.put(Index,Change);

            PartSnippet = PartSnippet.substring(PartSnippet.indexOf("<tr id=")+1);

            FullSnippet = FullSnippet.substring(FullSnippet.indexOf("</script><table id")+1);

//            Log.i("IRP: FullSnippet", FullSnippet);
        }

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