package com.chanb.zoos.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseSearchDiary extends SQLiteOpenHelper {


    public DatabaseSearchDiary(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQLite Database로 쿼리 실행 db.execSQL(sb.toString());
        StringBuffer sb = new StringBuffer();
        sb.append(" CREATE TABLE DIARYSEARCH ( ");
        sb.append(" _NO INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(" SEARCH TEXT) ");
        db.execSQL(sb.toString());
    }

    public void init() {
        SQLiteDatabase db = getReadableDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE DIARYSEARCH";
        db.execSQL(query);
        StringBuffer sb = new StringBuffer();
        sb.append(" CREATE TABLE DIARYSEARCH ( ");
        sb.append(" _NO INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(" SEARCH TEXT) ");
        db.execSQL(sb.toString());
    }


    public void insert(String text, String TABLE_NAME) {
        SQLiteDatabase db = getReadableDatabase();
        int row = getProfilesCount(TABLE_NAME);
        String sqlInsert = "INSERT INTO " + TABLE_NAME + "(SEARCH) VALUES ('" + text + "')";
        db.execSQL(sqlInsert);
    }

    public int getProfilesCount(String TABLE_NAME) {
        SQLiteDatabase db = getReadableDatabase();
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public ArrayList<String> select(String tableName) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> temp = new ArrayList<>();
        String idQuery = "SELECT * FROM " + tableName +" ORDER BY _NO DESC"; // order by _no desc
        Cursor cursor = null;
        cursor = db.rawQuery(idQuery, null);

        try {
            int i = 0;
            while (cursor.moveToNext()) {
                // TEXT로 선언된 "text" 컬럼 값 가져오기.
                String text = cursor.getString(1);
                temp.add(text);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        cursor.close();
        return temp;
    }

    public void updateAlarm(String tableName, String alarm) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "UPDATE " + tableName + " SET ALARM = '" + alarm + "' WHERE _NO = 1";
        db.execSQL(query);
    }

    public void delete(String tableName) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "DELETE FROM " + tableName;
        db.execSQL(query);
    }

}


