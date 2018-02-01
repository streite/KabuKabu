package com.mairyu.app.kabukabu;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by roberts on 6/28/2017.
 */

//**********************************************************************************************
//***   DrawerItemClickListener: Navigation Menu
//**********************************************************************************************
public class Navigator implements ListView.OnItemClickListener {

    private Activity mActivity;

    public Navigator(Activity activity) {

        mActivity = activity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch(position) {

            case 0:

                Intent intentFront = new Intent(mActivity, GoogleSheets.class);
                mActivity.startActivity(intentFront);

                break;

            case 2:

                Intent intentTraining = new Intent(mActivity, WSJ.class);
                mActivity.startActivity(intentTraining);

                break;

            case 1:

                Intent intentDatabase = new Intent(mActivity, SettingsPage.class);
                mActivity.startActivity(intentDatabase);

                break;

            case 3:

                Intent intentMain = new Intent(mActivity, MainActivity.class);
                mActivity.startActivity(intentMain);

                break;

            case 4:

                Intent intentIndex = new Intent(mActivity, WorldIndexView.class);
                mActivity.startActivity(intentIndex);

                break;


        }
    }
}

