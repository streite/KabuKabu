package com.mairyu.app.kabukabu;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

//==================================================================================================
//===   FirebaseAccessStocks
//==================================================================================================
public class FirebaseAccessStocks extends AppCompatActivity {

    DatabaseReference mDatabase;
    FirebaseAuth mAuth;

    ArrayList<Stock> allStocks = new ArrayList<>();

    SQLStockHandler sqlStockHandler;

    String FirebaseMode;

    private SharedPreferences _appPrefs;

    //**********************************************************************************************
    //***   onCreate
    //**********************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.firebase_access_stocks);

        //------------------------------------------------------------------------------------------
        //---   Get Recipe Details
        //------------------------------------------------------------------------------------------

        Bundle extras = getIntent().getExtras();
        FirebaseMode = extras.getString("FIREBASE_MODE");

        //------------------------------------------------------------------------------------------
        //---   Preference/Settings
        //------------------------------------------------------------------------------------------

        _appPrefs = new SharedPreferences(getApplicationContext());

        //------------------------------------------------------------------------------------------
        //---   SQLite Setup
        //------------------------------------------------------------------------------------------

        sqlStockHandler = new SQLStockHandler(FirebaseAccessStocks.this,_appPrefs.getSQLStockDBName(),
                Integer.parseInt(_appPrefs.getSQLStockDBVersion()));

        allStocks = sqlStockHandler.getAllStocks();

        //------------------------------------------------------------------------------------------
        //---   Firebase Setup
        //------------------------------------------------------------------------------------------

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        mAuth.signInWithEmailAndPassword("streitenberger@gmail.com", "Tarumi6881*");

        Log.i("LOG: (FBAS)", "onStart - User: " + currentUser);

        //------------------------------------------------------------------------------------------
        //---   Write to Firebase ...
        //------------------------------------------------------------------------------------------

        if (FirebaseMode.equals("WRITE")) {

            HashMap<String, Stock> StockHash = new HashMap<String, Stock>();

            //--------------------------------------------------------------------------------------
            //---   Populate Hash with all recipes ...
            //--------------------------------------------------------------------------------------
            for (Stock tmpStock : allStocks) {

//                String FixKey = tmpStock.getAuthor().replace(" ","_");
//                FixKey = FixKey + "___" + tmpStock.getTitle().replace(" ","_");

                StockHash.put(tmpStock.getTicker(), tmpStock);
            }

            Toast.makeText(FirebaseAccessStocks.this, StockHash.size() + " infos uploaded", Toast.LENGTH_SHORT).show();

            // All I have to do is transfer the single Hash
            mDatabase.child("KabuKabu").setValue(StockHash);

        //------------------------------------------------------------------------------------------
        //---   Read from Firebase ...
        //------------------------------------------------------------------------------------------

        } else {

            //--------------------------------------------------------------------------------------
            //---   Listens to any change in the Firebase ...
            //--------------------------------------------------------------------------------------
            mDatabase.child("KabuKabu").addListenerForSingleValueEvent(new ValueEventListener() {

                //----------------------------------------------------------------------------------
                //---   Some data has changed ...
                //----------------------------------------------------------------------------------
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    //------------------------------------------------------------------------------
                    //---   Clean SQL reset (to be safe)
                    //------------------------------------------------------------------------------

                    purgeSQLDB();

                    Log.i("LOG: (FB)", "SQL purged, reading ...");

                    //------------------------------------------------------------------------------
                    //---
                    //------------------------------------------------------------------------------

                    HashMap<String, Stock> StockHash = new HashMap<String, Stock>();

                    StockHash = (HashMap<String, Stock>) snapshot.getValue();

                    Stock tmpStock = new Stock();

                    for (Object PlantValue : StockHash.values()) {

//                        tmpStock.setSQLId(((Long) ((HashMap) PlantValue).get("sqlid")).intValue());
//                        tmpStock.setAuthor((String) ((HashMap) PlantValue).get("author"));
//                        tmpStock.setTitle((String) ((HashMap) PlantValue).get("title"));
//                        tmpStock.setFeaturing((String) ((HashMap) PlantValue).get("featuring"));
//                        tmpStock.setOrder(((Long) ((HashMap) PlantValue).get("order")).intValue());
//                        tmpStock.setPublishYear((String) ((HashMap) PlantValue).get("publishYear"));
//                        tmpStock.setSource((String) ((HashMap) PlantValue).get("source"));
//                        tmpStock.setReadCount(((Long) ((HashMap) PlantValue).get("readCount")).intValue());
//                        tmpStock.setSummary((String) ((HashMap) PlantValue).get("summary"));

                        sqlStockHandler.addStock(tmpStock);
                    }

                    Toast.makeText(FirebaseAccessStocks.this, StockHash.size() + " CarInfos downloaded", Toast.LENGTH_SHORT).show();
                }

                //----------------------------------------------------------------------------------
                //---   Some error has occured ...
                //----------------------------------------------------------------------------------
                @Override
                public void onCancelled(DatabaseError databaseError) {

                    Log.i("LOG: (FB)", "FB cancelled ...");
                }
            });
        }

        setResult(Activity.RESULT_OK);

        finish();
    }

    //**********************************************************************************************
    //***   Empty SQL DB (for clean slate)
    //**********************************************************************************************
    public void purgeSQLDB(){

        ArrayList<Stock> tmpStock = new ArrayList<>();
        tmpStock = sqlStockHandler.getAllStocks();

        if (tmpStock.size() > 0) {

            for (int i = 0; i < tmpStock.size(); i++) {

                Stock currentStock = tmpStock.get(i);

                sqlStockHandler.deleteStock(currentStock);
            }
        }
    }
}
