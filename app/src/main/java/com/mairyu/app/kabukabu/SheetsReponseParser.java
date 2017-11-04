package com.mairyu.app.kabukabu;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class SheetsReponseParser {

    //**********************************************************************************************
    //***   Parse Sheet response list for objects
    //**********************************************************************************************
    public static ArrayList<Stock> getSheetsStuff(List<List<Object>> SheetResponse, String Sheet) {

        ArrayList<Stock> allSheetsItems = new ArrayList<>();

        Log.i("LOG: (SJP) PARSER", "ARRAY "+SheetResponse);

        DateFormat df = new SimpleDateFormat("MM-dd-yyyy");

        //--------------------------------------------------------------------------------------
        //---   Loop over all Objects and collect them in ArrayList
        //--------------------------------------------------------------------------------------
        for (List<Object> ListObject : SheetResponse) {

            Stock sheetStock = new Stock();

            //--------------------------------------------------------------------------------------
            //---   Sheet specific allocation
            //--------------------------------------------------------------------------------------

            sheetStock.setCompany(ListObject.get(0).toString());
            sheetStock.setTicker(ListObject.get(1).toString());
            sheetStock.setPrice(Float.parseFloat(ListObject.get(2).toString()));
            sheetStock.setCategory(ListObject.get(3).toString());
            sheetStock.setSubcategory(ListObject.get(4).toString());
            sheetStock.setShares(Integer.parseInt(ListObject.get(5).toString()));
            sheetStock.setBasis(Float.parseFloat(ListObject.get(6).toString()));
            sheetStock.setDate(ListObject.get(7).toString());
            sheetStock.setCommission(Integer.parseInt(ListObject.get(8).toString()));
            sheetStock.setLeverage(ListObject.get(9).toString());
            sheetStock.setWatch(Integer.parseInt(ListObject.get(10).toString()));

            //--------------------------------------------------------------------------------------
            //---   Add flashcard to array
            //--------------------------------------------------------------------------------------
            try {

                allSheetsItems.add(sheetStock);

            } catch (Exception e) {

                Log.i("LOG: (SJP) CATCH", "RETURN");
            }
        }

        Log.i("LOG: (SJP)", "SIZE "+allSheetsItems.size());

        //--------------------------------------------------------------------------------------
        //---   Debug: Print Array
        //--------------------------------------------------------------------------------------
        if (allSheetsItems.size() > 0) {

            for (int j = 0; j < allSheetsItems.size(); j++) {

                Stock currentItem = allSheetsItems.get(j);

                Log.i("LOG: (SJP) Index------", j+"");
            }
        }

        return allSheetsItems;
    }
}