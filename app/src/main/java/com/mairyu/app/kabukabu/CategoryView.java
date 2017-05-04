package com.mairyu.app.kabukabu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

    }

//    //**********************************************************************************************
//    //***   Custom Array Adapter for DatabasePage
//    //**********************************************************************************************
//    private class CustomDatabaseAdapter extends ArrayAdapter<String> {
//
//        public CustomDatabaseAdapter() {
//
//            super(CategoryView.this, R.layout.category_list_view_item,CategoryArray);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            View itemView = convertView;
//
//            if (itemView == null) {
//
//                itemView = getLayoutInflater().inflate(R.layout.category_list_view_item, parent, false);
//            }
//
//            // Find the current card to work with
//            String currentString = CategoryMenu.get(position);
//
//            TextView menuOption = (TextView) itemView.findViewById(R.id.txtCategory);
//            menuOption.setText(currentString);
//
//            return itemView;
//        }
//    }
}
