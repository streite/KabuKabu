package com.mairyu.app.kabukabu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_view);

        //----------------------------------------------------------------------------------
        //---   Navigation Drawer
        //----------------------------------------------------------------------------------

        navigation = getResources().getStringArray(R.array.navigation);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,navigation));
        mDrawerList.setOnItemClickListener(new Navigator(this));

        //------------------------------------------------------------------------------------------
        //---   ListView Setup
        //------------------------------------------------------------------------------------------

        CategoryArray = getResources().getStringArray(R.array.categories);
//        List<String> Lines = Arrays.asList(getResources().getStringArray(R.array.Lines));

//        adapterCategory = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,CategoryArray);
        adapterCategory = new CustomDatabaseAdapter();

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
            String currentString = CategoryArray[position];

            TextView menuOption = (TextView) itemView.findViewById(R.id.txtStockGroup);
            menuOption.setText(currentString);

            return itemView;
        }
    }

    //**********************************************************************************************
    //***   onCreateOptionsMenu (Toolbar)
    //**********************************************************************************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),
                R.drawable.gradient_dark_grey_bg, null));

        MenuItem menu_spinner = menu.findItem(R.id.menu_spinner);
        menu_spinner.setVisible(false);

        MenuItem menu_edit = menu.findItem(R.id.menu_edit);
        menu_edit.setVisible(false);

        MenuItem menu_refresh = menu.findItem(R.id.menu_refresh);
        menu_refresh.setVisible(false);

        //------------------------------------------------------------------------------------------
        //---   Touch header -> Category
        //------------------------------------------------------------------------------------------
//        txtCategory.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View arg0, MotionEvent arg1) {
//
//                switch (arg1.getAction()) {
//
//                    case MotionEvent.ACTION_DOWN: {
//
//                        Intent intentTraining = new Intent(CategoryView.this, MainActivity.class);
//                        startActivity(intentTraining);
//
//                        break;
//                    }
//                }
//
//                return true;
//            }
//        });

        return true;
    }

    //**********************************************************************************************
    //***   onOptionsItemSelected (Toolbar)
    //**********************************************************************************************
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
