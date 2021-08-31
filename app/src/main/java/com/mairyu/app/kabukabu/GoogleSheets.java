package com.mairyu.app.kabukabu;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.ClearValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

//==================================================================================================
//===   GoogleSheets
//==================================================================================================
public class GoogleSheets extends AppCompatActivity {

    GoogleAccountCredential googleAccountCredential;

    ProgressDialog mProgress;

    private PreferenceSettings _appPrefs;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    static final int REQUEST_PREFERENCE = 2000;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS };

    private String sheetsID;
    private String sheetsTab;
    private String sheetsRange;
    private String sheetsMode;

    private ArrayList<Stock> allStocks = new ArrayList<>();

    private SQLStockHandler sqlHandler;

    // Client ID: 421675712394-0702fv31pk2q2dpsvfqus8n2hi9o4bu9.apps.googleusercontent.com

    // API Key: AIzaSyDiRSy8bUtlKlIGxZw1MwCSjwbzBCb2eLI
    // String mScope="oauth2:server:client_id:123456789-dgrgfdgfdgfdgngemhmtfko16f5tnobqphb6v.apps.googleusercontent.com:api_scope:https://www.googleapis.com/auth/plus.login"

    // from signingReport (@work)
    //    ----------
    //    Variant: debugAndroidTest
    //    Config: debug
    //    Store: C:\Users\roberts\.android\debug.keystore
    //    Alias: AndroidDebugKey
    //    MD5: 6C:18:93:73:78:70:05:0B:88:1C:99:41:4A:53:B7:BF
    //    SHA1: 72:4E:A6:A6:BB:60:FE:E5:23:BD:46:86:C8:91:83:AF:2E:C4:38:A4
    //    Valid until: Saturday, April 13, 2047

    //**********************************************************************************************
    //***   onCreate
    //**********************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_sheets);

        //------------------------------------------------------------------------------------------
        //---   Preference/Settings
        //------------------------------------------------------------------------------------------

        _appPrefs = new PreferenceSettings(getApplicationContext());

        //------------------------------------------------------------------------------------------
        //---   Get Card Details
        //------------------------------------------------------------------------------------------

        Bundle extras = getIntent().getExtras();

        sheetsID = extras.getString("SHEETS_ID");
        sheetsTab = extras.getString("SHEETS_TAB");
        sheetsRange = extras.getString("SHEETS_RANGE");
        sheetsMode = extras.getString("SHEETS_MODE");

        //------------------------------------------------------------------------------------------
        //---   Initialize credentials and service object
        //------------------------------------------------------------------------------------------

        googleAccountCredential = GoogleAccountCredential.usingOAuth2(this,Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        //------------------------------------------------------------------------------------------
        //---   SQLite Setup
        //------------------------------------------------------------------------------------------

        sqlHandler = new SQLStockHandler(GoogleSheets.this,_appPrefs.getSQLStockDBName(),
                Integer.parseInt(_appPrefs.getSQLStockDBVersion()));

        //------------------------------------------------------------------------------------------
        //---   Initialize credentials and service object
        //------------------------------------------------------------------------------------------

        getResultsFromApi();
    }

    //**********************************************************************************************
    //***   Attempt to call the API, after verifying that all the preconditions are satisfied:
    //***   (1) Google Play Services installed
    //***   (2) An account was selected
    //***   (3) The device currently has online access
    //***   Otherwise prompt
    //**********************************************************************************************
    private void getResultsFromApi() {

        try {

            String AccountName = googleAccountCredential.getSelectedAccountName();

            Log.i("LOG: (GS) TRY", "Account Name " + AccountName);

        } catch (Exception e) {

            e.printStackTrace();
        }

        //------------------------------------------------------------------------------------------
        //---   Check for Google Play Services, if not, aquire
        //------------------------------------------------------------------------------------------
        if (! isGooglePlayServicesAvailable()) {

            Log.i("LOG: (GS) API", "Google Play Service not available ...");

            acquireGooglePlayServices();

        //------------------------------------------------------------------------------------------
        //---   Check if account is selected
        //------------------------------------------------------------------------------------------
        } else if (googleAccountCredential.getSelectedAccountName() == null) {

            Log.i("LOG: (GS) API", "Account is null ... Choose Account");

            chooseAccount();

            String AccountName = googleAccountCredential.getSelectedAccountName();

            Log.i("LOG: (GS) API", "After selection the account Name " + AccountName);

        //------------------------------------------------------------------------------------------
        //---   Check whether device is online
        //------------------------------------------------------------------------------------------
        } else if (! isDeviceOnline()) {

            Log.i("LOG: (GS) API", "Device not online ...");

        //------------------------------------------------------------------------------------------
        //---   Finally, ready to go ... Launch AsyncTask
        //------------------------------------------------------------------------------------------
        } else {

            Log.i("LOG: (GS) API", "----------------- Make API Request ... ----------------------");

            new MakeRequestTask(GoogleSheets.this,googleAccountCredential).execute();
        }
    }

    //**********************************************************************************************
    //***   ASYNCTASK: An asynchronous task that handles the Google Sheets API call
    //**********************************************************************************************
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {

        private com.google.api.services.sheets.v4.Sheets mService = null;

        private Exception mLastError = null;

        //------------------------------------------------------------------------------------------
        //---   Constructor
        //------------------------------------------------------------------------------------------
        MakeRequestTask(Context context,GoogleAccountCredential credential) {

            mProgress = new ProgressDialog(context);

            HttpTransport transport = AndroidHttp.newCompatibleTransport();

            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API")
                    .build();
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: onPreExecute
        //------------------------------------------------------------------------------------------
        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            if (sheetsMode.equals("READ")) {
                mProgress.setTitle("Downloading Info from Google Sheets ... Please Wait");
            } else {
                mProgress.setTitle("Uploading Info to Google Sheets ... Please Wait");
            }
            mProgress.show();
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: doInBackground
        //------------------------------------------------------------------------------------------
        @Override
        protected List<String> doInBackground(Void... params) {

            try {

                String range;
                String valueInputOption = "RAW";
                List<String> results = new ArrayList<String>();
                ValueRange response  = null;

                try {

                    //------------------------------------------------------------------------------
                    //---   READ
                    //------------------------------------------------------------------------------

                    if (sheetsMode.equals("READ")) {

                        Log.i("LOG: (GS) ASYNC-TRY", "Google API Background ... READ");

                        range = sheetsRange + "!A1:K"+"1000";

                        response = this.mService.spreadsheets().values().get(sheetsID, range).execute();

                        Log.i("LOG: (GS) ASYNC-TRY", "response ..." + response);

                        List<List<Object>> values = response.getValues();

                        Log.i("LOG: (GS) ASYNC-TRY", "Values ... " + values);

                        allStocks = SheetsReponseParser.getSheetsStuff(values,sheetsRange);

                        //--------------------------------------------------------------------------------------
                        //---   Transfer downloaded flashcards into SQL DB
                        //--------------------------------------------------------------------------------------

                        purgeDB();

                        Log.i("LOG: (GS) ASYNC-TRY", "SIZE ... " + allStocks.size());

                        if (allStocks.size() > 0) {

                            for (int j = 0; j < allStocks.size(); j++) {

                                Stock tmpFlash = allStocks.get(j);

                                sqlHandler.addStock(tmpFlash);
                            }
                        }

                    //------------------------------------------------------------------------------
                    //---   WRITE
                    //------------------------------------------------------------------------------

                    } else if (sheetsMode.equals("WRITE")) {

                        allStocks = sqlHandler.getAllStocks();
                        Stock currentStock;
                        int stackSize = allStocks.size();

                        range = sheetsTab + "!A1:K" + stackSize;

                        List<List<Object>> writeValues = new ArrayList<>();

                        //------------------------------------------------------------------------------------------
                        //---   Loop over all stocks
                        //------------------------------------------------------------------------------------------
                        for (int sqlIndex = 0; sqlIndex < stackSize; sqlIndex++) {

                            currentStock = allStocks.get(sqlIndex);

                            List<Object> data = new ArrayList<>();

                            data.add(currentStock.getCompany());
                            data.add(currentStock.getTicker());
                            data.add(currentStock.getPrice());
                            data.add(currentStock.getCategory());
                            data.add(currentStock.getSubcategory());
                            data.add(currentStock.getShares());
                            data.add(currentStock.getBasis());
                            data.add(currentStock.getDate());
                            data.add(currentStock.getCommission());
                            data.add(currentStock.getLeverage());
                            data.add(currentStock.getWatch());

                            writeValues.add(data);
                        }

                        ValueRange requestBody = new ValueRange();
                        requestBody.setRange(range);
                        requestBody.setMajorDimension("ROWS");
                        requestBody.setValues(writeValues);

                        Log.i("LOG: (GS) ASYNC-TRY", "WRITE - requestBody " + requestBody);
                        Log.i("LOG: (GS) ASYNC-TRY", "WRITE - writeValues " + writeValues);

                        String clearRange = sheetsTab + "!A1:K" + 500;
                        ClearValuesRequest clearBody = new ClearValuesRequest();
                        Sheets.Spreadsheets.Values.Clear clearRequest =
                                mService.spreadsheets().values().clear(sheetsID,clearRange,clearBody);
                        ClearValuesResponse clearResponse = clearRequest.execute();

                        Sheets.Spreadsheets.Values.Update request =
                                mService.spreadsheets().values().update(sheetsID, range, requestBody);
                        request.setValueInputOption(valueInputOption);

                        UpdateValuesResponse responseWrite = request.execute();

                        Log.i("LOG: (GS) ASYNC-TRY", "WRITE - response ... " + responseWrite);
                    }

                } catch (UserRecoverableAuthIOException e) {

                    startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);

                } catch (IOException e) {

                    e.printStackTrace();

                    Log.i("LOG: (GS) ASYNC CATCH", "Get Data from API (2) - Error: "+e);
                }

                return results;

            } catch (Exception e) {

                e.printStackTrace();

                Log.i("LOG: (GS) ASYNC-CATCH", "Google API Background ..." + e);

                mLastError = e;
                cancel(true);
                return null;
            }
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: onPostExecute -
        //------------------------------------------------------------------------------------------
        @Override
        protected void onPostExecute(List<String> output) {

            mProgress.hide();

            if (output == null || output.size() == 0) {
//                mOutputText.setText("No results returned.");
            } else {
                output.add(0, "Data retrieved using the Google Sheets API:");
//                mOutputText.setText(TextUtils.join("\n", output));
            }

            // Not sure why I need this ... it shows up as CANCELLED otherwise
            setResult(Activity.RESULT_OK);

            finish();
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: onCancelled -
        //------------------------------------------------------------------------------------------
        @Override
        protected void onCancelled() {

            Log.i("LOG: (GS) ASYNC", "onCancelled");

            mProgress.hide();

//            if (mLastError != null) {
//                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
//                    showGooglePlayServicesAvailabilityErrorDialog(
//                            ((GooglePlayServicesAvailabilityIOException) mLastError)
//                                    .getConnectionStatusCode());
//                } else if (mLastError instanceof UserRecoverableAuthIOException) {
//                    startActivityForResult(
//                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
//                            FrontPage.REQUEST_AUTHORIZATION);
//                } else {
//                }
//            } else {
//            }
        }
    }

    //**********************************************************************************************
    //***   Check that Google Play services APK is installed and up to date
    //**********************************************************************************************
    private boolean isGooglePlayServicesAvailable() {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);

        return (connectionStatusCode == ConnectionResult.SUCCESS);
    }

    //**********************************************************************************************
    //***   Attempt to resolve a missing, out-of-date, invalid or disabled Google Play Services
    //***   installation via a user dialog, if possible ...
    //**********************************************************************************************
    private void acquireGooglePlayServices() {

        GoogleApiAvailability apiAvailability =  GoogleApiAvailability.getInstance();

        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {

            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    //**********************************************************************************************
    //***   Checks whether the device currently has a network connection.
    //***   @return true if the device has a network connection, false otherwise.
    //**********************************************************************************************
    private boolean isDeviceOnline() {

//        Log.i("LOG: (GS) ONLINE", "Check whether Device is online ...");

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }

    //**********************************************************************************************
    //***   Attempts to set the account used with the API credentials. If an account name was
    //***   previously saved it will use that one; otherwise an account picker dialog will be shown.
    //***   Note that the setting the account to use with the credentials object requires the app to
    //***   have the GET_ACCOUNTS permission, which is requested here if it is not already present.
    //***   The AfterPermissionGranted annotation indicates that this function will be rerun
    //***   automatically whenever the GET_ACCOUNTS permission is granted.
    //**********************************************************************************************
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {

        //------------------------------------------------------------------------------------------
        //---   Permission found, prompt for choosing account (existing/add)
        //------------------------------------------------------------------------------------------
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {

            String accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);

            //--------------------------------------------------------------------------------------
            //---   Account found in Preferences
            //--------------------------------------------------------------------------------------
            if (accountName != null) {

                Log.i("LOG: (GS) ACCOUNT", "Valid account found ...");

                googleAccountCredential.setSelectedAccountName(accountName);

                getResultsFromApi();

                //--------------------------------------------------------------------------------------
                //---   Start a dialog from which the user can choose an account
                //--------------------------------------------------------------------------------------
            } else {

                Log.i("LOG: (GS) ACCOUNT", "null account found ... query for account");

                startActivityForResult(googleAccountCredential.newChooseAccountIntent(),REQUEST_ACCOUNT_PICKER);
            }

            //------------------------------------------------------------------------------------------
            //---   No Permission found, prompt for GET_ACCOUNTS permission
            //------------------------------------------------------------------------------------------
        } else {

            Log.i("LOG: (GS) ACCOUNT", "No Easy Permission ...");

            EasyPermissions.requestPermissions(
                    this,"This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,Manifest.permission.GET_ACCOUNTS);
        }
    }

    //**********************************************************************************************
    //***   Display an error dialog showing that Google Play Services is missing or out of date.
    //***   @param connectionStatusCode code describing the presence/lack of Google Play Services
    //**********************************************************************************************
    void showGooglePlayServicesAvailabilityErrorDialog (final int connectionStatusCode) {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        Dialog dialog = apiAvailability.getErrorDialog(
                GoogleSheets.this,connectionStatusCode,REQUEST_GOOGLE_PLAY_SERVICES);

        dialog.show();
    }

    //**********************************************************************************************
    //***   onActivityResult
    //**********************************************************************************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {

            //--------------------------------------------------------------------------------------
            //---   Google Play Services
            //--------------------------------------------------------------------------------------
            case REQUEST_GOOGLE_PLAY_SERVICES:

                if (resultCode != RESULT_OK) {

                } else {

                    getResultsFromApi();
                }
                break;

            //--------------------------------------------------------------------------------------
            //---   Google Account Selection
            //--------------------------------------------------------------------------------------
            case REQUEST_ACCOUNT_PICKER:

                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {

                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

                    if (accountName != null) {

                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        googleAccountCredential.setSelectedAccountName(accountName);

                        getResultsFromApi();
                    }
                }

                break;

            //--------------------------------------------------------------------------------------
            //---   authorization
            //--------------------------------------------------------------------------------------
            case REQUEST_AUTHORIZATION:

                if (resultCode == RESULT_OK) {

                    getResultsFromApi();
                }

                break;
        }
    }

    //**********************************************************************************************
    //***   Empty SQL DB (for clean slate)
    //**********************************************************************************************
    public void purgeDB(){

//        this.deleteDatabase(_appPrefs.getSQLDBName());

        ArrayList<Stock> tmpFlashcards = new ArrayList<>();
        tmpFlashcards = sqlHandler.getAllStocks();

        if (tmpFlashcards.size() > 0) {

            for (int i = 0; i < tmpFlashcards.size(); i++) {

                Stock currentFlash = tmpFlashcards.get(i);

                sqlHandler.deleteStock(currentFlash);
            }
        }
    }
}