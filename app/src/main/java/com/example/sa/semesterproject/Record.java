package com.example.sa.semesterproject;

/**
 * Created by SA on 12/20/2017.
 */

public class Record {
    private int day;
    private int month;
    private int year;

    public Record(){

    }

    public Record(int month, int year){
        this.month = month;
        this.year = year;
    }

    public Record(int day, int month, int year){
        this.day = day;
        this.month = month;
        this.year = year;
    }


    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }


}
