package com.mairyu.app.kabukabu;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

//==================================================================================================
//===   WSJ
//==================================================================================================
public class WSJ extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Stock> allLosers = new ArrayList<>();
    private ArrayAdapter<Stock> adapterStocks;
    private ListView listViewAllStocks;

    private SQLStockHandler sqlHandler;

    private PreferenceSettings _appPrefs;

    private TextView txtHTML;
    private Button btnWSJFilterVolume;

    String fullResponse;

    //**********************************************************************************************
    //***   onCreate
    //**********************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.wsj_view);

        //------------------------------------------------------------------------------------------
        //---   Preference/Settings
        //------------------------------------------------------------------------------------------

        _appPrefs = new PreferenceSettings(getApplicationContext());

        //------------------------------------------------------------------------------------------
        //---   Layout
        //------------------------------------------------------------------------------------------

        btnWSJFilterVolume = (Button) findViewById(R.id.btnWSJFilterVolume);
        btnWSJFilterVolume.setOnClickListener(WSJ.this);

        //------------------------------------------------------------------------------------------
        //---   ListView Setup
        //------------------------------------------------------------------------------------------

        listViewAllStocks = (ListView) findViewById(R.id.listViewAllStocks);
        adapterStocks = new CustomDatabaseAdapter();
        listViewAllStocks.setAdapter(adapterStocks);

        registerForContextMenu(listViewAllStocks);

        //------------------------------------------------------------------------------------------
        //---   SQLite Setup
        //------------------------------------------------------------------------------------------

        sqlHandler = new SQLStockHandler(WSJ.this,_appPrefs.getSQLStockDBName(),
                Integer.parseInt(_appPrefs.getSQLStockDBVersion()));

        //------------------------------------------------------------------------------------------
        //---   Setup HTTP client for websites API server
        //------------------------------------------------------------------------------------------
        WJSAsyncStuff wsjAsyncStuff = new WJSAsyncStuff(WSJ.this);

        wsjAsyncStuff.execute();
    }

    //**********************************************************************************************
    //***   Custom Array Adapter for PortfolioPage
    //**********************************************************************************************
    private class CustomDatabaseAdapter extends ArrayAdapter<Stock> {

        public CustomDatabaseAdapter() {

            super(WSJ.this, R.layout.wsj_list_view_item, allLosers);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View itemView = convertView;

            if (itemView == null) {

                itemView = getLayoutInflater().inflate(R.layout.wsj_list_view_item, parent, false);
            }

            Stock currentStock = allLosers.get(position);

//            Price = currentStock.getPrice();
//            Shares = currentStock.getShares();
            String ChangePerc = currentStock.getChangePerc();
            String Volume = currentStock.getVolume();
//            Basis = currentStock.getBasis();
//            Comission = currentStock.getCommission();

            //--------------------------------------------------------------------------------------
            //---   Ticker
            //--------------------------------------------------------------------------------------
            TextView menuOption = (TextView) itemView.findViewById(R.id.portfolioListViewTicker);
            menuOption.setText(currentStock.getTicker());

//            menuOption = (TextView) itemView.findViewById(R.id.portfolioListViewPrice);
//            menuOption.setText(currentStock.getPrice()+"");
//
//            menuOption = (TextView) itemView.findViewById(R.id.portfolioListViewChangePerc);
//            menuOption.setText(currentStock.getChangePerc());

            //--------------------------------------------------------------------------------------
            //---   Change (Percentage)
            //------------------------------------------------------------------------------------------
            menuOption = (TextView) itemView.findViewById(R.id.portfolioListViewChangePerc);
            if (currentStock.getChangePerc().contains("+")) {
                menuOption.setTextColor(ContextCompat.getColor(WSJ.this, R.color.colorGreenStrong));
            } else {
                menuOption.setTextColor(ContextCompat.getColor(WSJ.this, R.color.colorRedStrong));
            }
//            ChangePerc = ChangePerc.replace("%","");
            String ChangePercFormat = String.format("%.02f", Float.parseFloat(currentStock.getChangePerc()));
            if (ChangePerc.equals("")) {
//                menuOption.setText(df1.format(Float.parseFloat(ChangePerc)) + "%");
            } else {
                menuOption.setText(ChangePercFormat + "%");
            }

            menuOption = (TextView) itemView.findViewById(R.id.portfolioListViewCompany);
            menuOption.setText(currentStock.getCompany());

            menuOption = (TextView) itemView.findViewById(R.id.portfolioListViewVolume);
            menuOption.setText(currentStock.getVolume());

            //------------------------------------------------------------------------------------------
//            menuOption = (TextView) itemView.findViewById(R.id.portfolioListViewGainLossPerc);
//            menuOption.setText(currentStock.getVolume());

            return itemView;
        }
    }

    //**********************************************************************************************
    //***   onClick (Button)
    //**********************************************************************************************
    @Override
    public void onClick(View v) {


        //------------------------------------------------------------------------------------------
        //---   Button ID
        //------------------------------------------------------------------------------------------
        switch (v.getId()) {

            //--------------------------------------------------------------------------------------
            //---   BUTTON: Word
            //--------------------------------------------------------------------------------------
            case R.id.btnWSJFilterVolume:

                int position = 0;

                ArrayList<Integer> FilterVolume = new ArrayList<Integer>();

                for (Stock stock : allLosers) {

                    if (Integer.parseInt(stock.getVolume().replaceAll(",", "")) > 100000) {

                        Log.i("LOG: (WSJ) VOLUME", "Volume: " + stock.getTicker());

                    } else {

                        FilterVolume.add(position);
                    }
                    position++;
                }

                Log.i("LOG: (WSJ) VOLUME", "Count: " + adapterStocks.getCount());

//                Stock toRemove = adapterStocks.getItem(2);
//                adapterStocks.remove(toRemove);

                int heap = 0;

                for (int i : FilterVolume) {

                    Log.i("LOG: (WSJ) VOLUME", "Volume: " + i + (i-heap));

                    Stock toRemove = adapterStocks.getItem(i-heap);
                    adapterStocks.remove(toRemove);
                    heap++;
                }

                break;
        }
    }

    //**********************************************************************************************
    //***   Pull Data from website, call Parser etc.
    //**********************************************************************************************
    private class WJSAsyncStuff extends AsyncTask<String, Void, String> {

        Context context;

        ProgressDialog progressDialog;


        public WJSAsyncStuff(Context context) {

            this.context = context;
            progressDialog = new ProgressDialog(context);
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: onPreExecute
        //------------------------------------------------------------------------------------------
        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            progressDialog.setTitle("Downloading Info From WSJ Web Page ... Please Wait");
            progressDialog.show();
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: doInBackground
        //------------------------------------------------------------------------------------------
        @Override
        protected String doInBackground(String... params) {

            InputStream stream = null;
            HttpURLConnection httpURLConnection = null;
            String result = null;
            String url = "http://online.wsj.com/mdc/public/page/2_3021-losecomp-loser.html";

            try {
                httpURLConnection = (HttpURLConnection) (new URL(url)).openConnection();

            } catch (IOException io) {

                Log.i("LOG: (WJS) CATCH", "httpURLConnection ... " + httpURLConnection);
            }

            try {

                httpURLConnection.setRequestMethod("GET");

            } catch (ProtocolException pe) {

            }

            //----------------------------------------------------------------------------------
            //---   open a communications link to the resource referenced by the URL
            //----------------------------------------------------------------------------------
            try {

                httpURLConnection.connect();

                Log.i("LOG: (KV) TRY", "httpURLConnection ... " + httpURLConnection);

            } catch (SocketTimeoutException ste) {

                Log.i("LOG: (KV) CATCH", "connect-SocketTimeoutException ... " + ste);

            } catch (IOException io) {

                Log.i("LOG: (KV) CATCH", "connect-IOException ... " + io);
            }

            //----------------------------------------------------------------------------------
            //---   get the status code from an HTTP response message
            //----------------------------------------------------------------------------------
            try {
                int responseCode = httpURLConnection.getResponseCode();

                Log.i("LOG: (KV) TRY", "responseCode ... " + responseCode);

            } catch (IOException io) {

                Log.i("LOG: (KV) CATCH", "responseCode-IOException ... " + io);
            }
            // Retrieve the response body as an InputStream.
            //----------------------------------------------------------------------------------
            //---   return an input stream that reads from this open connection
            //----------------------------------------------------------------------------------
            try {
                stream = httpURLConnection.getInputStream();
            } catch (IOException io) {

//                } catch (UnknownServiceException use) {
            }

            try {

                if (stream != null) {
                    // Converts Stream to String with max length of 500.
                    result = readStream(stream, 500000);
                }
            } catch (IOException io) {

            }

            Log.i("LOG: (KV) TRY", "stream ..."+stream);
            Log.i("LOG: (KV) TRY", "result ..."+result);

            // Close Stream and disconnect HTTPS connection.
            try {
                if (stream != null) {
                    stream.close();
                }
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            } catch (IOException i) {

            }

            return result;
        }

        //------------------------------------------------------------------------------------------
        //---   AsyncTask: onPostExecute - Display in ListView
        //------------------------------------------------------------------------------------------
        @Override
        protected void onPostExecute(String HTML_Source) {

            super.onPostExecute(HTML_Source);

            Log.i("LOG: (KV) POST", "HTML "+HTML_Source);

            allLosers = WSJResponseParser.getTopLoser(HTML_Source);

            listViewAllStocks = (ListView) findViewById(R.id.listViewAllStocks);
            adapterStocks = new CustomDatabaseAdapter();
            listViewAllStocks.setAdapter(adapterStocks);
            adapterStocks.notifyDataSetChanged();


            if (progressDialog.isShowing()) {

                progressDialog.dismiss();
            }
        }
    }

    private String readStream(InputStream stream, int maxLength) throws IOException {
        String result = null;
        // Read InputStream using the UTF-8 charset.
        InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
        // Create temporary buffer to hold Stream data with specified max length.
        char[] buffer = new char[maxLength];
        // Populate temporary buffer with Stream data.
        int numChars = 0;
        int readSize = 0;
        while (numChars < maxLength && readSize != -1) {
            numChars += readSize;
            int pct = (100 * numChars) / maxLength;
            readSize = reader.read(buffer, numChars, buffer.length - numChars);
        }
        if (numChars != -1) {
            // The stream was not empty.
            // Create String that is actual length of response body if actual length was less than
            // max length.
            numChars = Math.min(numChars, maxLength);
            result = new String(buffer, 0, numChars);
        }
        return result;
    }

    //**********************************************************************************************
    //***   onCreateContextMenu (Context Menu)
    //**********************************************************************************************
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Select The Action");

        menu.add(0, v.getId(), 0, "Track");
    }

    //**********************************************************************************************
    //***   onContextItemSelected (Context Menu)
    //**********************************************************************************************
    @Override
    public boolean onContextItemSelected(MenuItem item){

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        CharSequence Title = item.getTitle();
        View view;
        Stock currentStock = allLosers.get(info.position);

        switch (Title.toString()) {

            //--------------------------------------------------------------------------------------
            //---   EDIT
            //--------------------------------------------------------------------------------------
            case "Track":

                Stock newStock = new Stock();

                newStock.setTicker(currentStock.getTicker());
                newStock.setCategory("WSJ_LOSER");
                newStock.setShares(0);
                newStock.setBasis(currentStock.getPrice());
                newStock.setCommission(10);

                sqlHandler.addStock(newStock);

                break;
        }

        return true;
    }

    //**********************************************************************************************
    //***   onCreateOptionsMenu (Toolbar)
    //**********************************************************************************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),
                R.drawable.gradient_dark_yellow_bg, null));

        MenuItem menu_spinner = menu.findItem(R.id.menu_spinner);
        menu_spinner.setVisible(false);

        MenuItem menu_refresh = menu.findItem(R.id.menu_refresh);
        menu_refresh.setVisible(false);

        //------------------------------------------------------------------------------------------
        //---   Header
        //------------------------------------------------------------------------------------------

        TextView txtCategory = (TextView) findViewById(R.id.txtCategory);
        txtCategory.setText("Worst 100 (WJS)");
        txtCategory.setTextColor(ContextCompat.getColor(this, R.color.colorYellow1));

        return true;
    }

    //**********************************************************************************************
    //***   onOptionsItemSelected (Toolbar)
    //**********************************************************************************************
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //------------------------------------------------------------------------------------------
        //---   Pull down 'Settings' Menu
        //------------------------------------------------------------------------------------------
        if (id == R.id.menu_refresh) {

        }

        //------------------------------------------------------------------------------------------
        //---   Update and return to Portfolio
        //------------------------------------------------------------------------------------------
        if (id == R.id.menu_edit) {

        }

        return super.onOptionsItemSelected(item);
    }
}
