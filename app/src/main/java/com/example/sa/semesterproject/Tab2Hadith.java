package com.example.sa.semesterproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by SA on 12/7/2017.
 */

public class Tab2Hadith extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2_hadith, container, false);

        rootView.setBackgroundColor(getResources().getColor(R.color.colorWhite));

        Spinner hadithSpinner = (Spinner) rootView.findViewById(R.id.spinnerHadith);
        final ImageView image = (ImageView) rootView.findViewById(R.id.imageHadith);

        ArrayAdapter<String> hadithAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.hadith));
        hadithAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hadithSpinner.setAdapter(hadithAdapter);
        hadithSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0){
                    Toast.makeText(getContext(), "Hadith of Fasting : " + parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                    switch (position){
                        case 1:
                            image.setImageResource(R.drawable.hadith1);
                            break;
                        case 2:
                            image.setImageResource(R.drawable.hadith2);
                            break;
                        case 3:
                            image.setImageResource(R.drawable.hadith3);
                            break;
                        case 4:
                            image.setImageResource(R.drawable.hadith4);
                            break;
                        case 5:
                            image.setImageResource(R.drawable.hadith5);
                            break;
                        case 6:
                            image.setImageResource(R.drawable.hadith6);
                            break;
                        case 7:
                            image.setImageResource(R.drawable.hadith7);
                            break;
                        case 8:
                            image.setImageResource(R.drawable.hadith8);
                            break;
                        case 9:
                            image.setImageResource(R.drawable.hadith9);
                            break;
                        case 10:
                            image.setImageResource(R.drawable.hadith10);
                            break;
                        case 11:
                            image.setImageResource(R.drawable.hadith11);
                            break;
                        case 12:
                            image.setImageResource(R.drawable.hadith12);
                            break;
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;
    }

}