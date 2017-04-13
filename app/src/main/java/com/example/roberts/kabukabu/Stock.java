package com.example.roberts.kabukabu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class Stock extends AppCompatActivity {

    private String Ticker;
    private float Price;

    TextView txtStockTicker;
    TextView txtStockPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock);

        txtStockTicker = (TextView) findViewById(R.id.txtStockTicker);
        txtStockPrice = (TextView) findViewById(R.id.txtStockPrice);
    }
}
