package com.mairyu.app.kabukabu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnPortfolio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //------------------------------------------------------------------------------------------
        //---   Layout
        //------------------------------------------------------------------------------------------

        btnPortfolio = (Button) findViewById(R.id.btnPortfolio);
        btnPortfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //------------------------------------------------------------------------------------------
                //---   Setup Layout
                //------------------------------------------------------------------------------------------

                switch (v.getId()) {

                    //--------------------------------------------------------------------------------------
                    //---   SAVE
                    //--------------------------------------------------------------------------------------
                    case R.id.btnPortfolio:

                        Intent intentCategory = new Intent(MainActivity.this, CategoryView.class);
                        startActivity(intentCategory);

                        break;

                }
            }
        });
    }
}
