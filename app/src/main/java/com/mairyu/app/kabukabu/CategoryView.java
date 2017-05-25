package com.mairyu.app.kabukabu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

//==================================================================================================
//===   CategoryView
//==================================================================================================
public class CategoryView extends AppCompatActivity {

    private ArrayAdapter<String> adapterCategory;

    private List<String> CategoryMenu = new ArrayList<String>();

    private ListView categoryListView;

    private String[] CategoryArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_view);

        //------------------------------------------------------------------------------------------
        //---   ListView Setup
        //------------------------------------------------------------------------------------------

        CategoryArray = getResources().getStringArray(R.array.categories);

        adapterCategory = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,CategoryArray);
        categoryListView = (ListView) findViewById(R.id.categoryListView);
        categoryListView.setAdapter(adapterCategory);

//        registerForContextMenu(categoryListView);
        //------------------------------------------------------------------------------------------
        //---   Listen for Click (ListView)
        //------------------------------------------------------------------------------------------

        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {

                Intent intentTraining = new Intent(CategoryView.this, PortfolioView.class);
                intentTraining.putExtra("PORTFOLIO_CATEGORY", CategoryArray[position]);
                startActivity(intentTraining);

                switch (position) {

                    // Training
                    case 0:

                      break;

                }
            }
        });

    }

    //**********************************************************************************************
    //***   Custom Array Adapter for DatabasePage
    //**********************************************************************************************
    private class CustomDatabaseAdapter extends ArrayAdapter<String> {

        public CustomDatabaseAdapter() {

            super(CategoryView.this, R.layout.category_list_view_item,CategoryArray);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View itemView = convertView;

            if (itemView == null) {

                itemView = getLayoutInflater().inflate(R.layout.category_list_view_item, parent, false);
            }

            // Find the current card to work with
            String currentString = CategoryMenu.get(position);

            TextView menuOption = (TextView) itemView.findViewById(R.id.txtStockGroup);
            menuOption.setText(currentString);

            return itemView;
        }
    }
}
