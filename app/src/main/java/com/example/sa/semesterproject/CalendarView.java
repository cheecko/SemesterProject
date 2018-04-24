package com.example.sa.semesterproject;


import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

/**
 * Created by SA on 12/7/2017.
 */

public class CalendarView extends LinearLayout {
    // for logging
    private static final String LOGTAG = "Calendar View";

    // how many days to show, defaults to six weeks, 42 days
    private static final int DAYS_COUNT = 42;

    // default date format
    private static final String DATE_FORMAT = "MMM yyyy";

    // date format
    private String dateFormat;

    // current displayed month
    private Calendar currentDate = Calendar.getInstance();

    //event handling
    private EventHandler eventHandler = null;

    final DBHandler db = new DBHandler(getContext());

    // internal components
    private LinearLayout header;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView grid;
    private TextView countMonThu;
    private TextView countNotFasting;
    private TextView countFasting;
    int ThisMonth = 0;
    int Fasting = 0;

    // seasons' rainbow
    int[] rainbow = new int[] {
            R.color.summer,
            R.color.fall,
            R.color.winter,
            R.color.spring
    };

    // month-season association (northern hemisphere, sorry australia :)
    int[] monthSeason = new int[] {2, 2, 3, 3, 3, 0, 0, 0, 1, 1, 1, 2};

    public CalendarView(Context context) {
        super(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context, attrs);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context, attrs);
    }

    /**
     * Load control xml layout
     */
    private void initControl(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.calendar_layout, this);

        loadDateFormat(attrs);
        assignUiElements();
        //assignClickHandlers();

        updateCalendar();
    }

    private void loadDateFormat(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarView);

        try {
            // try to load provided date format, and fallback to default otherwise
            dateFormat = ta.getString(R.styleable.CalendarView_dateFormat);
            if (dateFormat == null)
                dateFormat = DATE_FORMAT;
        }
        finally {
            ta.recycle();
        }
    }

    private void assignUiElements() {
        // layout is inflated, assign local variables to components
        header = (LinearLayout) findViewById(R.id.calendar_header);
        btnPrev = (ImageView) findViewById(R.id.buttonLeft);
        btnNext = (ImageView) findViewById(R.id.buttonRight);
        txtDate = (TextView) findViewById(R.id.current_date);
        grid = (GridView) findViewById(R.id.calendar_grid);
        countMonThu = (TextView) findViewById(R.id.countMonThu);
        countNotFasting = (TextView) findViewById(R.id.countNotFasting);
        countFasting = (TextView) findViewById(R.id.countFasting);
    }

   /* private void assignClickHandlers() {

    }*/

    /**
     * Display dates correctly in grid
     */
    public void calculate(int count){
        int day;
        Calendar calendar = (Calendar) currentDate.clone();
        // Note that month is 0-based in calendar, bizarrely.
        calendar.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (day = 1; day <= daysInMonth; day++) {
            calendar.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), day);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.THURSDAY) {
                ThisMonth++;
            }
        }
        Fasting = ThisMonth - count;

        countMonThu.setText(String.valueOf(ThisMonth));
        countNotFasting.setText(String.valueOf(count));
        countFasting.setText(String.valueOf(Fasting));
        ThisMonth = 0;
    }

    public void updateCalendar() {
        updateCalendar(null);
    }

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar(final HashSet<Date> events) {
        try {
            ArrayList<Date> cells = new ArrayList<>();
            Calendar calendar = (Calendar) currentDate.clone();

            // determine the cell for current month's beginning
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

            // move calendar backwards to the beginning of the week
            calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

            // fill cells
            while (cells.size() < DAYS_COUNT) {
                cells.add(calendar.getTime());
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            int count = db.countEvent(new Record(currentDate.get(Calendar.MONTH), currentDate.get(Calendar.YEAR) - 1900));
            calculate(count);

            final List<Record> records = db.getAllRecord1();
            for(Record record: records){
                events.add(new Date(record.getYear(), record.getMonth(), record.getDay()));
            }

            /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, addLegend(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });*/

            // update grid
            grid.setAdapter(new CalendarAdapter(getContext(), cells, events));

            // add one month and refresh UI
            btnNext.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*for(Record record: records){
                        events.remove(new Date(record.getYear(), record.getMonth(), record.getDay()));
                    }*/
                    currentDate.add(Calendar.MONTH, 1);
                    updateCalendar();
                    eventHandler.setEvents();
                    int count = db.countEvent(new Record(currentDate.get(Calendar.MONTH), currentDate.get(Calendar.YEAR) - 1900));
                    calculate(count);
                }
            });

            // subtract one month and refresh UI
            btnPrev.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*for(Record record: records){
                        events.remove(new Date(record.getYear(), record.getMonth(), record.getDay()));
                    }*/
                    currentDate.add(Calendar.MONTH, -1);
                    updateCalendar();
                    eventHandler.setEvents();
                    int count = db.countEvent(new Record(currentDate.get(Calendar.MONTH), currentDate.get(Calendar.YEAR) - 1900));
                    calculate(count);
                }
            });

            // long-pressing a day
            grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(final AdapterView<?> view, final View cell, final int position, long id) {
                    // handle long-press
                    if (eventHandler == null)
                        return false;

                    eventHandler.onDayLongPress((Date)view.getItemAtPosition(position));
                    Date today = new Date();
                    final Date date = (Date)view.getItemAtPosition(position);
                    final int day = date.getDate();
                    final int month = date.getMonth();
                    final int year = date.getYear();
                    int a = date.getDay();

                    try {
                        if((a == Calendar.MONDAY - 1 || a == Calendar.THURSDAY - 1) && ((ColorDrawable)cell.getBackground()).getColor() == getResources().getColor(R.color.colorLightBlue)){
                            if (month != today.getMonth() || year != today.getYear()) {
                                // if this day is outside current month, grey it out
                                if(((TextView) cell).getCurrentTextColor() == getResources().getColor(R.color.colorRed)){
                                    ((TextView) cell).setTextColor(getResources().getColor(R.color.colorRed));
                                }else{
                                    ((TextView) cell).setTextColor(getResources().getColor(R.color.greyed_out));
                                }
                            }else{
                                if(day != today.getDate()){
                                    if(((TextView) cell).getCurrentTextColor() == getResources().getColor(R.color.colorRed)){
                                        ((TextView) cell).setTextColor(getResources().getColor(R.color.colorRed));
                                    }else{
                                        ((TextView) cell).setTextColor(getResources().getColor(R.color.colorBlack34));
                                    }
                                }else{
                                    ((TextView) cell).setTextColor(getResources().getColor(R.color.today));
                                }
                            }
                            cell.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorWhite));
                            events.add(date);
                            db.addEvent1(new Record(day, month, year));
                            int count = db.countEvent(new Record(month, year));
                            calculate(count);

                        }else if((a == Calendar.MONDAY - 1 || a == Calendar.THURSDAY - 1) && ((ColorDrawable)cell.getBackground()).getColor() == getResources().getColor(R.color.colorWhite)){
                            Log.d("a : " + a, "onItemLongClick: ");
                            if (month != today.getMonth() || year != today.getYear()) {
                                // if this day is outside current month, grey it out
                                if(((TextView) cell).getCurrentTextColor() == getResources().getColor(R.color.colorRed)){
                                    ((TextView) cell).setTextColor(getResources().getColor(R.color.colorRed));
                                }else{
                                    ((TextView) cell).setTextColor(getResources().getColor(R.color.greyed_out));
                                }
                            }else{
                                if(day != today.getDate()){
                                    if(((TextView) cell).getCurrentTextColor() == getResources().getColor(R.color.colorRed)){
                                        ((TextView) cell).setTextColor(getResources().getColor(R.color.colorRed));
                                    }else{
                                        ((TextView) cell).setTextColor(getResources().getColor(R.color.colorBlack34));
                                    }
                                }else{
                                    ((TextView) cell).setTextColor(getResources().getColor(R.color.today));
                                }
                            }
                            cell.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorLightBlue));
                            events.remove(date);
                            db.deleteEvent1(new Record(day, month, year));
                            int count = db.countEvent(new Record(month, year));
                            calculate(count);
                        }else{
                            if(((ColorDrawable)cell.getBackground()).getColor() == getResources().getColor(R.color.colorWhite)){
                                String[] fastingName = getResources().getStringArray(R.array.fasting_name);
                                TypedArray fastingColor = getResources().obtainTypedArray(R.array.fasting_color);
                                final List<Fasting> list = new ArrayList<Fasting>();

                                for(int i = 0; i < fastingName.length; i++){
                                    list.add(new Fasting(fastingName[i], fastingColor.getDrawable(i)));
                                }

                                ListAdapter listAdapter = new CustomAlertDialogListViewAdapter(getContext(), list);

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Choose you fasting : ");
                                builder.setAdapter(listAdapter, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Fasting fastingNameChoose = list.get(which);
                                        Toast.makeText(getContext(), "Choose : " + fastingNameChoose.getFastingName(), Toast.LENGTH_SHORT).show();
                                        final List<Record2> records = db.getAllRecord2();
                                        switch (which){
                                            case 0:
                                                cell.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorRamadhan));
                                                db.addEvent2(new Record2(day, month, year, R.color.colorRamadhan));
                                                break;
                                            case 1:
                                                cell.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorSyawal));
                                                db.addEvent2(new Record2(day, month, year, R.color.colorSyawal));
                                                break;
                                            case 2:
                                                cell.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorPuasaPertengahanBulan));
                                                db.addEvent2(new Record2(day, month, year, R.color.colorPuasaPertengahanBulan));
                                                break;
                                            case 3:
                                                cell.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorZulhijjah));
                                                db.addEvent2(new Record2(day, month, year, R.color.colorZulhijjah));
                                                break;
                                            case 4:
                                                cell.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorMuharram));
                                                db.addEvent2(new Record2(day, month, year, R.color.colorMuharram));
                                                break;
                                        }
                                    }
                                });
                                builder.setCancelable(true);
                                builder.show();
                                if (month != today.getMonth() || year != today.getYear()) {
                                    // if this day is outside current month, grey it out
                                    if(((TextView) cell).getCurrentTextColor() == getResources().getColor(R.color.colorRed)){
                                        ((TextView) cell).setTextColor(getResources().getColor(R.color.colorRed));
                                    }else{
                                        ((TextView) cell).setTextColor(getResources().getColor(R.color.greyed_out));
                                    }
                                }else{
                                    if(day != today.getDate()){
                                        if(((TextView) cell).getCurrentTextColor() == getResources().getColor(R.color.colorRed)){
                                            ((TextView) cell).setTextColor(getResources().getColor(R.color.colorRed));
                                        }else{
                                            ((TextView) cell).setTextColor(getResources().getColor(R.color.colorBlack34));
                                        }
                                    }else{
                                        ((TextView) cell).setTextColor(getResources().getColor(R.color.today));
                                    }
                                }
                            }else{
                                final List<Record2> records = db.getAllRecord2();
                                for(Record2 record: records){
                                    if(day == record.getDay() && month == record.getMonth() && year == record.getYear()){
                                        cell.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorWhite));
                                        Log.d("delete event : " + record, "onClick: ");
                                        db.deleteEvent2(new Record2(day, month, year));
                                    }
                                }
                                if(((ColorDrawable)cell.getBackground()).getColor() != getResources().getColor(R.color.colorWhite)){
                                    cell.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorWhite));
                                    db.addEvent2(new Record2(day, month, year, R.color.colorWhite));
                                }


                                if (month != today.getMonth() || year != today.getYear()) {
                                    // if this day is outside current month, grey it out
                                    if(((TextView) cell).getCurrentTextColor() == getResources().getColor(R.color.colorRed)){
                                        ((TextView) cell).setTextColor(getResources().getColor(R.color.colorRed));
                                    }else{
                                        ((TextView) cell).setTextColor(getResources().getColor(R.color.greyed_out));
                                    }
                                }else{
                                    if(day != today.getDate()){
                                        if(((TextView) cell).getCurrentTextColor() == getResources().getColor(R.color.colorRed)){
                                            ((TextView) cell).setTextColor(getResources().getColor(R.color.colorRed));
                                        }else{
                                            ((TextView) cell).setTextColor(getResources().getColor(R.color.colorBlack34));
                                        }
                                    }else{
                                        ((TextView) cell).setTextColor(getResources().getColor(R.color.today));
                                    }
                                }
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });

            // update title
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            txtDate.setText(sdf.format(currentDate.getTime()));

            // set header color according to current season
            int month = currentDate.get(Calendar.MONTH);
            int season = monthSeason[month];
            int color = rainbow[season];
            header.setBackgroundColor(getResources().getColor(color));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int numberOfDaysInMonth(int month, int year) {
        Calendar monthStart = new GregorianCalendar(year, month, 1);
        return monthStart.getActualMaximum(Calendar.DAY_OF_MONTH);
    }


    private class CalendarAdapter extends ArrayAdapter<Date> {
                // days with events
          private HashSet<Date> eventDays;

                // for view inflation
          private LayoutInflater inflater;

          public CalendarAdapter(Context context, ArrayList<Date> days, HashSet<Date> eventDays) {
                  super(context, R.layout.calendar_layout_day, days);
                  this.eventDays = eventDays;
                  inflater = LayoutInflater.from(context);
          }

          @Override
          public View getView(int position, View view, ViewGroup parent) {
                // day in question
                Date date = getItem(position);
                int day = date.getDate();
                int month = date.getMonth();
                int year = date.getYear();
                final int a = date.getDay();

                // today
                Date today = new Date();

                /*int count = db.countEvent(new Record(month+1, year));
                calculate(count);
                countNotFasting.setText(String.valueOf(count));*/

                // inflate item if it does not exist yet
                if (view == null)
                    view = inflater.inflate(R.layout.calendar_layout_day, parent, false);

                // clear styling
                ((TextView)view).setTypeface(null, Typeface.NORMAL);
                ((TextView)view).setTextColor(Color.BLACK);

                view.setBackgroundResource(R.color.colorWhite);

                //Monday Thursday
                if(a == 1 || a == 4){
                    view.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorLightBlue));
                    ((TextView) view).setTextColor(getResources().getColor(R.color.colorBlack34));
                }

                // Ayyamul Bidh / Puasa Pertengahan Bulan
                int[] puasa3day2017 = new int[]{12, 10, 12, 10, 10, 0, 7, 6, 4, 3, 2, 2};
                int[] puasa3month2017 = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
                int puasa3year = 117;
                int LongOfPuasa3 = 3;
                for(int i = 0; i < 12; i++){
                    int numberOfDayspuasa3 = numberOfDaysInMonth(puasa3day2017[i], puasa3month2017[i]);
                    for(int j = puasa3day2017[i]; j < puasa3day2017[i] + LongOfPuasa3; j++){
                        if(j <= numberOfDayspuasa3) {
                            if (day == j && month == puasa3month2017[i] && year == puasa3year) {
                                view.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorPuasaPertengahanBulan));
                                ((TextView) view).setTextColor(getResources().getColor(R.color.colorBlack34));
                            }
                        }else{
                            if (day == j - numberOfDayspuasa3 && month == puasa3month2017[i] + 1 && year == puasa3year) {
                                view.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorPuasaPertengahanBulan));
                                ((TextView) view).setTextColor(getResources().getColor(R.color.colorBlack34));
                            }
                        }
                    }
                }

                int[] puasa3day2018 = new int[]{1, 30, 1, 31, 29, 27, 26, 26, 23, 22, 21, 21};
                int[] puasa3month2018 = new int[]{0, 0, 2, 2, 3, 5, 6, 7, 8, 9, 10, 11};
                puasa3year = 118;
                for(int i = 0; i < 12; i++){
                    int numberOfDayspuasa3 = numberOfDaysInMonth(puasa3day2018[i], puasa3month2018[i]);
                    for(int j = puasa3day2018[i]; j < puasa3day2018[i] + LongOfPuasa3; j++){
                        if(j <= numberOfDayspuasa3) {
                            if (day == j && month == puasa3month2018[i] && year == puasa3year) {
                                view.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorPuasaPertengahanBulan));
                                ((TextView) view).setTextColor(getResources().getColor(R.color.colorBlack34));
                            }
                        }else{
                            if (day == j - numberOfDayspuasa3 && month == puasa3month2018[i] + 1 && year == puasa3year) {
                                view.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorPuasaPertengahanBulan));
                                ((TextView) view).setTextColor(getResources().getColor(R.color.colorBlack34));
                            }
                        }
                    }
                }

                //Ramadhan
                int dayOfRamadhanStart = 17;
                int monthOfRamadhan = Calendar.MAY;
                int yearOfRamadhan = 118;
                int numberOfDaysRamadhan = numberOfDaysInMonth(monthOfRamadhan, yearOfRamadhan);
                int LongOfRamadhan = 28;
                for(int i = 0; i <= LongOfRamadhan; i++){
                    if(i + dayOfRamadhanStart <= numberOfDaysRamadhan){
                        if((day == i + dayOfRamadhanStart && month == monthOfRamadhan && year == yearOfRamadhan)) {
                            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorRamadhan));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.colorBlack34));
                        }
                    }else{
                        if((day == i + dayOfRamadhanStart - numberOfDaysRamadhan && month == monthOfRamadhan + 1 && year == yearOfRamadhan)) {
                            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorRamadhan));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.colorBlack34));
                        }
                    }
                }

                dayOfRamadhanStart = 28;
                monthOfRamadhan = Calendar.MAY;
                yearOfRamadhan = 117;
                numberOfDaysRamadhan = numberOfDaysInMonth(monthOfRamadhan, yearOfRamadhan);
                for(int i = 0; i < LongOfRamadhan; i++){
                    if(i + dayOfRamadhanStart <= numberOfDaysRamadhan){
                        if((day == i + dayOfRamadhanStart && month == monthOfRamadhan && year == yearOfRamadhan)) {
                            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorRamadhan));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.colorBlack34));
                        }
                    }else{
                        if((day == i + dayOfRamadhanStart - numberOfDaysRamadhan && month == monthOfRamadhan + 1 && year == yearOfRamadhan)) {
                            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorRamadhan));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.colorBlack34));
                        }
                    }
                }

                //Syawal
                int dayOfSyawalStart = 16;
                int monthOfSyawal = Calendar.JUNE;
                int yearOfSyawal = 118;
                int numberOfDaysSyawal = numberOfDaysInMonth(monthOfSyawal, yearOfSyawal);
                int LongOfSyawal = 44;
                for(int i = 0; i < LongOfSyawal; i++){
                    if(i + dayOfSyawalStart <= numberOfDaysSyawal){
                        if((day == i + dayOfSyawalStart && month == monthOfSyawal && year == yearOfSyawal)) {
                            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorSyawal));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.colorBlack34));
                        }
                    }else{
                        if((day == i + dayOfSyawalStart - numberOfDaysSyawal && month == monthOfSyawal + 1 && year == yearOfSyawal)) {
                            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorSyawal));
                            ((TextView) view).setTextColor(getResources().getColor(R.color.colorBlack34));
                        }
                    }
                }

                //Zulhijjah
                for(int i = 13; i <= 21; i++){
                    if(day == i && month == Calendar.AUGUST && year == 118){
                        view.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorZulhijjah));
                        ((TextView) view).setTextColor(getResources().getColor(R.color.colorBlack34));
                    }
                }

                //Muharram
                int dayOfMuharramStart = 19;
                int monthOfMuharram = Calendar.SEPTEMBER;
                int yearOfMuharram = 118;
                int LongOfMuharram = 2;
                for(int i = 0; i < LongOfMuharram; i++){
                    if(day == i + dayOfMuharramStart && month == monthOfMuharram && year == yearOfMuharram){
                        view.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorMuharram));
                        ((TextView) view).setTextColor(getResources().getColor(R.color.colorBlack34));
                    }
                }

                dayOfMuharramStart = 29;
                monthOfMuharram = Calendar.SEPTEMBER;
                yearOfMuharram = 117;
                for(int i = 0; i <= LongOfMuharram; i++){
                    if(day == i + dayOfMuharramStart && month == monthOfMuharram && year == yearOfMuharram){
                        view.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorMuharram));
                        ((TextView) view).setTextColor(getResources().getColor(R.color.colorBlack34));
                    }
                }

                // Cannot Fasting
                int[] cannotfastingday = new int[]{25, 1, 2, 3, 4};
                int[] cannotfastingmonth = new int[]{5, 8, 8, 8, 8};
                int cannotfastingyear = 117;
                for(int i = 0; i < cannotfastingday.length; i++){
                    if(day == cannotfastingday[i] && month == cannotfastingmonth[i] && year == cannotfastingyear){
                        view.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorCanNotFasting));
                        ((TextView) view).setTextColor(getResources().getColor(R.color.colorWhite));

                    }
                }

                /*cannotfastingday = new int[]{15, 22, 23, 24, 25, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
                cannotfastingmonth = new int[]{5, 7, 7, 7, 7, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4};
                cannotfastingyear = 118;
                for(int i = 0; i < cannotfastingday.length; i++){
                    if(day == cannotfastingday[i] && month == cannotfastingmonth[i] && year == cannotfastingyear){
                        view.setBackgroundColor(view.getContext().getResources().getColor(R.color.color_can_not_fasting));
                        ((TextView) view).setTextColor(getResources().getColor(R.color.colorWhite));

                    }
                }*/

              for (Date eventDate : eventDays) {
                  if (eventDate.getDate() == day && eventDate.getMonth() == month && eventDate.getYear() == year) {
                      view.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorWhite));
                      ((TextView) view).setTextColor(getResources().getColor(R.color.colorBlack34));
                  }
              }

                // if this day has an event, specify event
              final List<Record2> records = db.getAllRecord2();
              for(Record2 record: records){
                  if(record.getDay() == day && record.getMonth() == month && record.getYear() == year && record.getColor() == R.color.colorRamadhan){
                      view.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorRamadhan));
                      ((TextView) view).setTextColor(getResources().getColor(R.color.colorBlack34));
                  }else if(record.getDay() == day && record.getMonth() == month && record.getYear() == year && record.getColor() == R.color.colorSyawal){
                      view.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorSyawal));
                      ((TextView) view).setTextColor(getResources().getColor(R.color.colorBlack34));
                  }else if(record.getDay() == day && record.getMonth() == month && record.getYear() == year && record.getColor() == R.color.colorPuasaPertengahanBulan){
                      view.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorPuasaPertengahanBulan));
                      ((TextView) view).setTextColor(getResources().getColor(R.color.colorBlack34));
                  }else if(record.getDay() == day && record.getMonth() == month && record.getYear() == year && record.getColor() == R.color.colorZulhijjah){
                      view.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorZulhijjah));
                      ((TextView) view).setTextColor(getResources().getColor(R.color.colorBlack34));
                  }else if(record.getDay() == day && record.getMonth() == month && record.getYear() == year && record.getColor() == R.color.colorMuharram) {
                      view.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorMuharram));
                      ((TextView) view).setTextColor(getResources().getColor(R.color.colorBlack34));
                  }else if(record.getDay() == day && record.getMonth() == month && record.getYear() == year && record.getColor() == R.color.colorWhite) {
                      view.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorWhite));
                      ((TextView) view).setTextColor(getResources().getColor(R.color.colorBlack34));
                  }
              }



                // if this day is outside current month, grey it out
                if (month != today.getMonth() || year != today.getYear()) {
                    ((TextView)view).setTextColor(getResources().getColor(R.color.greyed_out));
                }

                // first day of month in Islamic Calendar
                int[] redday = new int[]{18, 17, 19, 17, 17, 15, 14, 13, 11, 10, 9, 9};
                int[] redmonth = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
                for(int i = 0; i < 12; i++){
                    if(day == redday[i] && month == redmonth[i] && year == 118){
                        ((TextView) view).setTextColor(getResources().getColor(R.color.colorRed));
                    }
                }

                // today
                if (day == today.getDate() && month == today.getMonth() && year == today.getYear()) {
                    // if it is today, set it to blue/bold
                    ((TextView)view).setTypeface(null, Typeface.BOLD);
                    ((TextView)view).setTextColor(getResources().getColor(R.color.today));
                }

                // remove day outside selected month
                int showMonth = currentDate.get(Calendar.MONTH);
                int showYear = currentDate.get(Calendar.MONTH);
                if(month != showMonth && year != showYear){
                    view.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorWhite));
                    ((TextView)view).setTextColor(getResources().getColor(R.color.colorWhite));
                }

                // set text
                ((TextView)view).setText(String.valueOf(date.getDate()));
                return view;
          }
    }

    /**
     * Assign event handler to be passed needed events
     */
    public void setEventHandler(EventHandler eventHandler) {
        try {
            this.eventHandler = eventHandler;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This interface defines what events to be reported to
     * the outside world
     */
    public interface EventHandler {
        void onDayLongPress(Date date);
        void setEvents();
    }

    public class Fasting{
        private String fastingName;
        private Drawable fastingColor;

        public Fasting(String fastingName, Drawable fastingColor){
            this.fastingName = fastingName;
            this.fastingColor = fastingColor;
        }

        public String getFastingName() {
            return fastingName;
        }

        public Drawable getFastingColor() {
            return fastingColor;
        }
    }

    //Snackbar (no use)
    /*private SpannableStringBuilder addLegend(){
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(" ");
        ImageSpan imageSpan1 = new ImageSpan(getContext(), R.drawable.color_ramadhan);
        spannableStringBuilder.setSpan(imageSpan1, spannableStringBuilder.length() - 1, spannableStringBuilder.length(), 0);
        spannableStringBuilder.append(" Ramadhan ");
        ImageSpan imageSpan2 = new ImageSpan(getContext(), R.drawable.color_syawal);
        spannableStringBuilder.setSpan(imageSpan2, spannableStringBuilder.length() - 1, spannableStringBuilder.length(), 0);
        spannableStringBuilder.append(" Syawal ");
        ImageSpan imageSpan3 = new ImageSpan(getContext(), R.drawable.color_puasa_pertengahan_bulan);
        spannableStringBuilder.setSpan(imageSpan3, spannableStringBuilder.length() - 1, spannableStringBuilder.length(), 0);
        spannableStringBuilder.append(" Ayyamul Bidh ");
        ImageSpan imageSpan4 = new ImageSpan(getContext(), R.drawable.color_can_not_fasting);
        spannableStringBuilder.setSpan(imageSpan4, spannableStringBuilder.length() - 1, spannableStringBuilder.length(), 0);
        spannableStringBuilder.append(" Cannot Fasting\n");
        spannableStringBuilder.append(" ");
        ImageSpan imageSpan5 = new ImageSpan(getContext(), R.drawable.color_zulhijjah);
        spannableStringBuilder.setSpan(imageSpan5, spannableStringBuilder.length() - 1, spannableStringBuilder.length(), 0);
        spannableStringBuilder.append(" Zulhijjah & Muharram ");
        ImageSpan imageSpan7 = new ImageSpan(getContext(), R.drawable.color_red);
        spannableStringBuilder.setSpan(imageSpan7, spannableStringBuilder.length() - 1, spannableStringBuilder.length(), 0);
        spannableStringBuilder.append(" First Day of Month ");

        return spannableStringBuilder;
    }*/

}