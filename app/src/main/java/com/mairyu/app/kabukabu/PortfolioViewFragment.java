package com.mairyu.app.kabukabu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//==================================================================================================
//===   PortfolioViewFragment
//==================================================================================================
public class PortfolioViewFragment extends Fragment {

    //**********************************************************************************************
    //***   onCreateView
    //**********************************************************************************************
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.portfolio_view_fragment,container,false);

        return rootView;
    }
}

