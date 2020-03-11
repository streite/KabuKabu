package com.mairyu.app.kabukabu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class SQLhandler extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "words";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_COMPANY = "company";
    private static final String COLUMN_TICKER = "ticker";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_CHANGE = "change";
    private static final String COLUMN_PERC_CHANGE = "perc_change";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_SUBCATEGORY = "subcategory";
    private static final String COLUMN_SHARES = "shares";
    private static final String COLUMN_BASIS = "basis";
    private static final String COLUMN_VOLUME = "volume";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_COMMISSION = "commission";
    private static final String COLUMN_LEVERAGE = "leverage";
    private static final String COLUMN_WATCH = "watch";


    //**********************************************************************************************
    //***   onCreate (is only run when the database file did not exist and was just created)
    //**********************************************************************************************
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_STOCK_TABLE = "CREATE TABLE " + TABLE_NAME +
                "("
                + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_COMPANY + " TEXT, "
                + COLUMN_TICKER + " TEXT, "
                + COLUMN_PRICE + " REAL, "
                + COLUMN_CHANGE + " TEXT, "
                + COLUMN_PERC_CHANGE + " TEXT, "
                + COLUMN_CATEGORY + " TEXT, "
                + COLUMN_SUBCATEGORY + " TEXT, "
                + COLUMN_SHARES + " INTEGER, "
                + COLUMN_BASIS + " REAL, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_VOLUME + " TEXT, "
                + COLUMN_COMMISSION + " INTEGER, "
                + COLUMN_LEVERAGE + " TEXT, "
                + COLUMN_WATCH + " INTEGER " +
                ")";

        Log.i("LOG: (SQL) ", "onCreate");

        // Create DB
        db.execSQL(CREATE_STOCK_TABLE);
    }

    //**********************************************************************************************
    //***   onUpgrade (is only called when the database file exists but the stored version number
    //***              is lower than requested in constructor)
    //**********************************************************************************************
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        Log.i("LOG: (SQL) onCreate", "onCreate ...");

        onCreate(db);
    }

    //**********************************************************************************************
    //***   CONSTRUCTOR
    //**********************************************************************************************
    public SQLhandler(Context context, String DATABASE_NAME, int DATABASE_VERSION) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //*********************************************************************************************
    //**   METHOD: Add (new) Stock to SQL
    //**********************************************************************************************
    public long addStock(Stock stock) {

        SQLiteDatabase database = SQLhandler.this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_COMPANY, stock.getCompany());
        values.put(COLUMN_TICKER, stock.getTicker());
        values.put(COLUMN_PRICE, stock.getPrice());
        values.put(COLUMN_CHANGE, stock.getChange());
        values.put(COLUMN_PERC_CHANGE, stock.getChangePerc());
        values.put(COLUMN_CATEGORY, stock.getCategory());
        values.put(COLUMN_SUBCATEGORY, stock.getSubcategory());
        values.put(COLUMN_SHARES, stock.getShares());
        values.put(COLUMN_BASIS, stock.getBasis());
        values.put(COLUMN_DATE, stock.getDate());
        values.put(COLUMN_VOLUME, stock.getVolume());
        values.put(COLUMN_COMMISSION, stock.getCommission());
        values.put(COLUMN_LEVERAGE, stock.getLeverage());
        values.put(COLUMN_WATCH, stock.getWatch());

        long ID = database.insert(TABLE_NAME, null, values);

        database.close();

        return (ID);
    }

    //**********************************************************************************************
    //***   METHOD: Retrieve single flashcard from SQL
    //**********************************************************************************************
    public Stock getStockByID(int id) {

        SQLiteDatabase database = this.getReadableDatabase();

        //------------------------------------------------------------------------------------------
        //---   Query from SQL
        //------------------------------------------------------------------------------------------
        Cursor cursor = database.query(TABLE_NAME, new String[]
                {COLUMN_ID, COLUMN_COMPANY,COLUMN_TICKER,COLUMN_PRICE,COLUMN_CHANGE,COLUMN_PERC_CHANGE,
                        COLUMN_CATEGORY,COLUMN_SUBCATEGORY,
                        COLUMN_SHARES,COLUMN_BASIS,COLUMN_DATE,COLUMN_VOLUME,
                        COLUMN_COMMISSION,COLUMN_LEVERAGE,COLUMN_WATCH},COLUMN_ID + "=?",
                new String [] {String.valueOf(id)},null,null,null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        //------------------------------------------------------------------------------------------
        //---   Copy SQL to flashcard
        //------------------------------------------------------------------------------------------

        Stock stock = new Stock();

        stock.setId(Integer.parseInt(cursor.getString(0)));
        stock.setCompany(cursor.getString(1));
        stock.setTicker(cursor.getString(2));
        stock.setPrice(Float.parseFloat(cursor.getString(3)));
        stock.setChange(cursor.getString(4));
        stock.setPercChange(cursor.getString(5));
        stock.setCategory(cursor.getString(6));
        stock.setSubcategory(cursor.getString(7));
        stock.setShares(Integer.parseInt(cursor.getString(8)));
        stock.setBasis(Float.parseFloat(cursor.getString(9)));
        stock.setDate(cursor.getString(10));
        stock.setVolume(cursor.getString(11));
        stock.setCommission(Integer.parseInt(cursor.getString(12)));
        stock.setLeverage(cursor.getString(13));
        stock.setWatch(Integer.parseInt(cursor.getString(14)));

        return stock;
    }

    //**********************************************************************************************
    //***   METHOD: Retrieve single Stock via from SQL
    //**********************************************************************************************
    public Stock getStocksByTicker(String ticker) {

        String selectAllQuery;

        SQLiteDatabase database = this.getReadableDatabase();

        ArrayList<Stock> stockArrayList = new ArrayList<>();

        selectAllQuery = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_TICKER + " =  \"" + ticker + "\"";

        Cursor cursor = database.rawQuery(selectAllQuery, null);

        if (cursor.moveToFirst()) {

            do {

                Stock stock = new Stock();

                //----------------------------------------------------------------------------------
                //---   Copy SQL to flashcard
                //----------------------------------------------------------------------------------
                stock.setId(Integer.parseInt(cursor.getString(0)));
                stock.setCompany(cursor.getString(1));
                stock.setTicker(cursor.getString(2));
                stock.setPrice(Float.parseFloat(cursor.getString(3)));
                stock.setChange(cursor.getString(4));
                stock.setPercChange(cursor.getString(5));
                stock.setCategory(cursor.getString(6));
                stock.setSubcategory(cursor.getString(7));
                stock.setShares(Integer.parseInt(cursor.getString(8)));
                stock.setBasis(Float.parseFloat(cursor.getString(9)));
                stock.setDate(cursor.getString(10));
                stock.setVolume(cursor.getString(11));
                stock.setCommission(Integer.parseInt(cursor.getString(12)));
                stock.setLeverage(cursor.getString(13));
                stock.setWatch(Integer.parseInt(cursor.getString(14)));

                stockArrayList.add(stock);

            } while (cursor.moveToNext());
        }

        if (stockArrayList.size() > 0) {
            return stockArrayList.get(0);
        } else {
            return (new Stock());
        }
    }

    //**********************************************************************************************
    //***   METHOD: Retrieve Stocks via Category from SQL
    //**********************************************************************************************
    public ArrayList<Stock> getStocksByCategory(String Main, String Sub) {

        String selectAllQuery;

        SQLiteDatabase database = this.getReadableDatabase();

        ArrayList<Stock> stockArrayList = new ArrayList<>();

        selectAllQuery = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_CATEGORY + " =  \"" + Main + "\" AND " + COLUMN_SUBCATEGORY + " =  \"" + Sub + "\"";

        Cursor cursor = database.rawQuery(selectAllQuery, null);

        if (cursor.moveToFirst()) {

            do {

                Stock stock = new Stock();

                //----------------------------------------------------------------------------------
                //---   Copy SQL to flashcard
                //----------------------------------------------------------------------------------
                stock.setId(Integer.parseInt(cursor.getString(0)));
                stock.setCompany(cursor.getString(1));
                stock.setTicker(cursor.getString(2));
                stock.setPrice(Float.parseFloat(cursor.getString(3)));
                stock.setChange(cursor.getString(4));
                stock.setPercChange(cursor.getString(5));
                stock.setCategory(cursor.getString(6));
                stock.setSubcategory(cursor.getString(7));
                stock.setShares(Integer.parseInt(cursor.getString(8)));
                stock.setBasis(Float.parseFloat(cursor.getString(9)));
                stock.setDate(cursor.getString(10));
                stock.setVolume(cursor.getString(11));
                stock.setCommission(Integer.parseInt(cursor.getString(12)));
                stock.setLeverage(cursor.getString(13));
                stock.setWatch(Integer.parseInt(cursor.getString(14)));

                stockArrayList.add(stock);

            } while (cursor.moveToNext());
        }

        return stockArrayList;
    }

    //**********************************************************************************************
    //***   METHOD: Retrieve all Stocks from a Subcategory
    //**********************************************************************************************
    public ArrayList<Stock> getStocksBySubcategory(String Category, String Subcategory) {

        String selectAllQuery;

        SQLiteDatabase database = this.getReadableDatabase();

        ArrayList<Stock> stockArrayList = new ArrayList<>();

        selectAllQuery = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_SUBCATEGORY + " =  \"" + Subcategory +
                                                        "\" AND " + COLUMN_CATEGORY + " =  \"" + Category + "\"";

        Cursor cursor = database.rawQuery(selectAllQuery, null);

        if (cursor.moveToFirst()) {

            do {

                Stock stock = new Stock();

                //----------------------------------------------------------------------------------
                //---   Copy SQL to flashcard
                //----------------------------------------------------------------------------------
                stock.setId(Integer.parseInt(cursor.getString(0)));
                stock.setCompany(cursor.getString(1));
                stock.setTicker(cursor.getString(2));
                stock.setPrice(Float.parseFloat(cursor.getString(3)));
                stock.setChange(cursor.getString(4));
                stock.setPercChange(cursor.getString(5));
                stock.setCategory(cursor.getString(6));
                stock.setSubcategory(cursor.getString(7));
                stock.setShares(Integer.parseInt(cursor.getString(8)));
                stock.setBasis(Float.parseFloat(cursor.getString(9)));
                stock.setDate(cursor.getString(10));
                stock.setVolume(cursor.getString(11));
                stock.setCommission(Integer.parseInt(cursor.getString(12)));
                stock.setLeverage(cursor.getString(13));
                stock.setWatch(Integer.parseInt(cursor.getString(14)));

                stockArrayList.add(stock);

            } while (cursor.moveToNext());
        }

        return stockArrayList;
    }

    //**********************************************************************************************
    //***   METHOD: Retrieve all flashcards from SQL
    //**********************************************************************************************
    public ArrayList<Stock> getAllStocks(){

        String selectAllQuery;

        ArrayList<Stock> stockArrayList = new ArrayList<>();

        SQLiteDatabase database = this.getReadableDatabase();

        selectAllQuery = "SELECT * FROM " + TABLE_NAME;

        Cursor cursor = database.rawQuery(selectAllQuery, null);

        if (cursor.moveToFirst()) {

            do {

                Stock stock = new Stock();

                //----------------------------------------------------------------------------------
                //---   Copy SQL to flashcard
                //----------------------------------------------------------------------------------
                stock.setId(Integer.parseInt(cursor.getString(0)));
                stock.setCompany(cursor.getString(1));
                stock.setTicker(cursor.getString(2));
                stock.setPrice(Float.parseFloat(cursor.getString(3)));
                stock.setChange(cursor.getString(4));
                stock.setPercChange(cursor.getString(5));
                stock.setCategory(cursor.getString(6));
                stock.setSubcategory(cursor.getString(7));
                stock.setShares(Integer.parseInt(cursor.getString(8)));
                stock.setBasis(Float.parseFloat(cursor.getString(9)));
                stock.setDate(cursor.getString(10));
                stock.setVolume(cursor.getString(11));
                stock.setCommission(Integer.parseInt(cursor.getString(12)));
                stock.setLeverage(cursor.getString(13));
                stock.setWatch(Integer.parseInt(cursor.getString(14)));

                stockArrayList.add(stock);

            } while (cursor.moveToNext());
        }

        return stockArrayList;
    }

    //**********************************************************************************************
    //***   METHOD: Update flashcard in SQL
    //**********************************************************************************************
    public int updateStock(Stock stock) {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_COMPANY, stock.getCompany());
        values.put(COLUMN_TICKER, stock.getTicker());
        values.put(COLUMN_PRICE, stock.getPrice());
        values.put(COLUMN_CHANGE, stock.getChange());
        values.put(COLUMN_PERC_CHANGE, stock.getChangePerc());
        values.put(COLUMN_CATEGORY, stock.getCategory());
        values.put(COLUMN_SUBCATEGORY, stock.getSubcategory());
        values.put(COLUMN_SHARES, stock.getShares());
        values.put(COLUMN_BASIS, stock.getBasis());
        values.put(COLUMN_DATE, stock.getDate());
        values.put(COLUMN_VOLUME, stock.getVolume());
        values.put(COLUMN_COMMISSION, stock.getCommission());
        values.put(COLUMN_LEVERAGE, stock.getLeverage());
        values.put(COLUMN_WATCH, stock.getWatch());

        return database.update(TABLE_NAME, values, COLUMN_ID + " = ? ",new String[]{String.valueOf(stock.getId())});
    }

    //**********************************************************************************************
    //***   METHOD: Delete single flashcard in SQL
    //**********************************************************************************************
    public void deleteStock(Stock stock) {

        SQLiteDatabase database = this.getWritableDatabase();

        database.delete(TABLE_NAME, COLUMN_ID + " = ?",new String[]{String.valueOf(stock.getId())});

        database.close();
    }

    //**********************************************************************************************
    //**   METHOD: Determine number of flashcards in SQL
    //**********************************************************************************************
    public int getStockCount() {

        String flashcardCountQuery = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery(flashcardCountQuery, null);
        //cursor.close();

        return cursor.getCount();
    }
}