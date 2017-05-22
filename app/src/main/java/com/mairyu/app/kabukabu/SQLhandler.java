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
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_SHARES = "shares";
    private static final String COLUMN_BASIS = "basis";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_COMMISSION = "commission";

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
                + COLUMN_CATEGORY + " TEXT, "
                + COLUMN_SHARES + " INTEGER, "
                + COLUMN_BASIS + " REAL, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_COMMISSION + " INTEGER" +
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

        onCreate(db);
    }

    //**********************************************************************************************
    //***   CONSTRUCTOR
    //**********************************************************************************************
    public SQLhandler(Context context, String DATABASE_NAME, int DATABASE_VERSION) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //*********************************************************************************************
    //**   METHOD: Add (new) flashcard to SQL
    //**********************************************************************************************
    public long addStock(Stock stock) {

        SQLiteDatabase database = SQLhandler.this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_COMPANY, stock.getCompany());
        values.put(COLUMN_TICKER, stock.getTicker());
        values.put(COLUMN_PRICE, stock.getPrice());
        values.put(COLUMN_CATEGORY, stock.getGroup());
        values.put(COLUMN_SHARES, stock.getShares());
        values.put(COLUMN_BASIS, stock.getBasis());
        values.put(COLUMN_DATE, stock.getDate());
        values.put(COLUMN_COMMISSION, stock.getCommission());

        long ID = database.insert(TABLE_NAME, null, values);

        database.close();

        return (ID);
    }

//    /
//*********************************************************************************************
//    /
//**   METHOD: Retrieve single flashcard from SQL
//    /
//*********************************************************************************************
//    public Flashcard getFlashcard(int id) {
//
//        SQLiteDatabase database = this.getReadableDatabase();
//
//        //------------------------------------------------------------------------------------------
//        //---   Query from SQL
//        //------------------------------------------------------------------------------------------
//        Cursor cursor = database.query(TABLE_NAME, new String[]
//                {COLUMN_ID, COLUMN_FOREIGN_CHAR,COLUMN_FOREIGN_ALT,COLUMN_FOREIGN_WORD,COLUMN_ENGLISH_WORD,
//                        COLUMN_KNOW_FOREIGN_CHAR,COLUMN_KNOW_FOREIGN_ALT,COLUMN_KNOW_ENGLISH_WORD,
//                        COLUMN_SLEEP_DATE,COLUMN_PRIORITY_TAG},COLUMN_ID + "=?",
//                new String [] {String.valueOf(id)},null,null,null);
//
//        if (cursor != null) {
//            cursor.moveToFirst();
//        }
//
//        //------------------------------------------------------------------------------------------
//        //---   Copy SQL to flashcard
//        //------------------------------------------------------------------------------------------
//
//        Flashcard flashcard = new Flashcard();
//
//        flashcard.setId(Integer.parseInt(cursor.getString(0)));
//        flashcard.setForeignChar(cursor.getString(1));
//        flashcard.setForeignAlt(cursor.getString(2));
//        flashcard.setForeignWord(cursor.getString(3));
//        flashcard.setEnglishWord(cursor.getString(4));
//        flashcard.setKnowForeignCharLevel(Integer.parseInt(cursor.getString(5)));
//        flashcard.setKnowForeignAltLevel(Integer.parseInt(cursor.getString(6)));
//        flashcard.setKnowEnglishWordLevel(Integer.parseInt(cursor.getString(7)));
//        flashcard.setSleepDate(cursor.getString(8));
//        flashcard.setPriorityTag(Boolean.parseBoolean(cursor.getString(9)));
//
//        return flashcard;
//    }
//
    //**********************************************************************************************
    //***   METHOD: Retrieve all flashcards from SQL
    //**********************************************************************************************
    public ArrayList<Stock> getAllStocks(boolean random){

        String selectAllQuery;

        ArrayList<Stock> stockList = new ArrayList<>();

        SQLiteDatabase database = this.getReadableDatabase();

        if (random) {
            selectAllQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY RANDOM()";
        } else {
            selectAllQuery = "SELECT * FROM " + TABLE_NAME;
        }

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
                stock.setGroup(cursor.getString(4));
                stock.setShares(Integer.parseInt(cursor.getString(5)));
                stock.setBasis(Float.parseFloat(cursor.getString(6)));
                stock.setDate(cursor.getString(7));
                stock.setCommission(Integer.parseInt(cursor.getString(8)));

                stockList.add(stock);

            } while (cursor.moveToNext());
        }

        return stockList;
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
        values.put(COLUMN_CATEGORY, stock.getGroup());
        values.put(COLUMN_SHARES, stock.getShares());
        values.put(COLUMN_BASIS, stock.getBasis());
        values.put(COLUMN_DATE, stock.getDate());
        values.put(COLUMN_COMMISSION, stock.getCommission());

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