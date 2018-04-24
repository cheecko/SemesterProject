package com.example.sa.semesterproject;


import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;


/**
 * Created by SA on 12/7/2017.
 */

public class Tab1Calendar extends Fragment {
    HashSet<Date> events = new HashSet<>();
    CalendarView cv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1_calendar, container, false);

        rootView.setBackgroundColor(getResources().getColor(R.color.colorWhite));

        /*events.add(new Date());
        events.add(new Date(117, 11, 3));
        events.add(new Date(117, 11, 26));
        events.add(new Date(117, 11, 31));*/
        java.text.SimpleDateFormat a = new java.text.SimpleDateFormat("dd.MM.yyyy");

        cv = ((CalendarView) rootView.findViewById(R.id.calendar_view));
        cv.updateCalendar(events);

        // assign event handler
        cv.setEventHandler(new CalendarView.EventHandler() {
            @Override
            public void onDayLongPress(Date date) {
                // show returned day

                java.text.DateFormat df = java.text.SimpleDateFormat.getDateInstance();
                Toast.makeText(getContext(), df.format(date), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void setEvents() {
                cv.updateCalendar(events);

            }
        });

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View viewBuilder = getLayoutInflater(savedInstanceState).inflate(R.layout.legend_dialog, null);
                builder.setTitle("Color Legend");
                builder.setView(viewBuilder);
                builder.setCancelable(true);
                builder.show();
            }
        });
        return rootView;
    }


}
