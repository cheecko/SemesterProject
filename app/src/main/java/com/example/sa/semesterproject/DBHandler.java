package com.example.sa.semesterproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SA on 12/20/2017.
 */

public class DBHandler extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RecordDB";
    public static final String TABLE_RECORD1 = "recordtable1";
    public static final String TABLE_RECORD2 = "recordtable2";
    public static final String KEY_ID = "id";
    public static final String KEY_DAY = "day";
    public static final String KEY_MONTH = "month";
    public static final String KEY_YEAR = "year";
    public static final String KEY_COLOR = "color";

    public DBHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RECORD_TABLE1 = "CREATE TABLE " + TABLE_RECORD1 + "(" + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_DAY + " INTEGER, "
                + KEY_MONTH + " INTEGER, "
                + KEY_YEAR + " INTEGER)";

        String CREATE_RECORD_TABLE2 = "CREATE TABLE " + TABLE_RECORD2 + "(" + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_DAY + " INTEGER, "
                + KEY_MONTH + " INTEGER, "
                + KEY_YEAR + " INTEGER, "
                + KEY_COLOR + " INTEGER)";
        db.execSQL(CREATE_RECORD_TABLE1);
        db.execSQL(CREATE_RECORD_TABLE2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORD1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORD2);
        onCreate(db);
    }

    public void addEvent1(Record record){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DAY, record.getDay());
        values.put(KEY_MONTH, record.getMonth());
        values.put(KEY_YEAR, record.getYear());
        db.insert(TABLE_RECORD1, null, values);
        db.close();
    }

    public void addEvent2(Record2 record){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DAY, record.getDay());
        values.put(KEY_MONTH, record.getMonth());
        values.put(KEY_YEAR, record.getYear());
        values.put(KEY_COLOR, record.getColor());
        db.insert(TABLE_RECORD2, null, values);
        db.close();
    }

    public List<Record> getAllRecord1(){
        List<Record> recordList = new ArrayList<Record>();
        String selectQuery = "SELECT * FROM " + TABLE_RECORD1;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                Record record = new Record();
                record.setDay(cursor.getInt(1));
                record.setMonth(cursor.getInt(2));
                record.setYear(cursor.getInt(3));
                recordList.add(record);
            }while (cursor.moveToNext());
        }

        return recordList;
    }

    public List<Record2> getAllRecord2(){
        List<Record2> recordList = new ArrayList<Record2>();
        String selectQuery = "SELECT * FROM " + TABLE_RECORD2;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                Record2 record = new Record2();
                record.setDay(cursor.getInt(1));
                record.setMonth(cursor.getInt(2));
                record.setYear(cursor.getInt(3));
                record.setColor(cursor.getInt(4));
                recordList.add(record);
            }while (cursor.moveToNext());
        }

        return recordList;
    }

    public void deleteEvent1(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + TABLE_RECORD1 + " WHERE "
                + KEY_DAY + " = " + record.getDay() + " AND "
                + KEY_MONTH + " = " + record.getMonth() + " AND "
                + KEY_YEAR + " = " + record.getYear();
        db.execSQL(deleteQuery);
        db.close();
    }

    public void deleteEvent2(Record2 record) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + TABLE_RECORD2 + " WHERE "
                + KEY_DAY + " = " + record.getDay() + " AND "
                + KEY_MONTH + " = " + record.getMonth() + " AND "
                + KEY_YEAR + " = " + record.getYear();
        db.execSQL(deleteQuery);
        db.close();
    }

    public int countEvent(Record record){
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT * FROM " + TABLE_RECORD1 + " WHERE "
                + KEY_MONTH + " = " + record.getMonth() + " AND "
                + KEY_YEAR + " = " + record.getYear();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }


}
