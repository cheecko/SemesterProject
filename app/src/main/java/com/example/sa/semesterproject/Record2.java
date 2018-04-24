package com.example.sa.semesterproject;

/**
 * Created by SA on 1/16/2018.
 */

public class Record2 {
    private int day;
    private int month;
    private int year;
    private int color;

    public Record2(){

    }

    public Record2(int month, int year){
        this.month = month;
        this.year = year;
    }

    public Record2(int day, int month, int year){
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public Record2(int day, int month, int year, int color){
        this.day = day;
        this.month = month;
        this.year = year;
        this.color = color;
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}