package com.example.sa.semesterproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by SA on 12/7/2017.
 */

public class Tab3AboutMe extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3_aboutme, container, false);

        rootView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        return rootView;
    }
}