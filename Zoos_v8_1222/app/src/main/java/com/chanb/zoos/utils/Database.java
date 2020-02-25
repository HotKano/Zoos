package com.chanb.zoos.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper {

    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // SQLite Database로 쿼리 실행 db.execSQL(sb.toString());
        StringBuffer sb = new StringBuffer();
        sb.append(" CREATE TABLE MEMBERINFO ( ");
        sb.append(" _NO INTEGER, ");
        sb.append(" ID TEXT, ");
        sb.append(" PASS TEXT, ");
        sb.append(" NICKNAME TEXT, ");
        sb.append(" ALARM TEXT ) ");
        db.execSQL(sb.toString());

        StringBuffer sb2 = new StringBuffer();
        sb2.append(" CREATE TABLE STORY ( ");
        sb2.append(" AUTO_PLAY TEXT ) ");
        db.execSQL(sb2.toString());
    }

    public void init() {
        SQLiteDatabase db = getReadableDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            String query = "DROP TABLE MEMBERINFO";
            db.execSQL(query);
            // SQLite Database로 쿼리 실행 db.execSQL(sb.toString());
            StringBuffer sb = new StringBuffer();
            sb.append(" CREATE TABLE MEMBERINFO ( ");
            sb.append(" _NO INTEGER, ");
            sb.append(" ID TEXT, ");
            sb.append(" PASS TEXT, ");
            sb.append(" NICKNAME TEXT, ");
            sb.append(" ALARM TEXT ) ");
            db.execSQL(sb.toString());

            String query2 = "DROP TABLE STORY";
            db.execSQL(query2);
            StringBuffer sb2 = new StringBuffer();
            sb2.append(" CREATE TABLE STORY ( ");
            sb2.append(" AUTO_PLAY TEXT ) ");
            db.execSQL(sb2.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void insert(String id, String pass, String nickname, String TABLE_NAME) {
        try {
            SQLiteDatabase db = getReadableDatabase();
            int row = getProfilesCount(TABLE_NAME);
            if (row != 0) {
                String[] test = select("MEMBERINFO");
                String sqlDelete = "DELETE FROM " + TABLE_NAME;
                db.execSQL(sqlDelete);
                String sqlInsert = "INSERT INTO " + TABLE_NAME + "(_NO, ID, PASS, ALARM, NICKNAME) VALUES (1, '" + id + "','" + pass + "','" + test[3] + "','" + nickname + "')";
                db.execSQL(sqlInsert);
            } else {
                String sqlInsert = "INSERT INTO " + TABLE_NAME + "(_NO, ID, PASS, ALARM, NICKNAME) VALUES (1, '" + id + "','" + pass + "','true' ,'" + nickname + "')";
                db.execSQL(sqlInsert);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getProfilesCount(String TABLE_NAME) {
        try {
            SQLiteDatabase db = getReadableDatabase();
            String countQuery = "SELECT  * FROM " + TABLE_NAME;
            Cursor cursor = db.rawQuery(countQuery, null);
            int count = cursor.getCount();
            cursor.close();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public String[] select(String tableName) {
        SQLiteDatabase db = getReadableDatabase();
        String[] temp = new String[4];
        String idQuery = "SELECT * FROM " + tableName;
        Cursor cursor = null;
        cursor = db.rawQuery(idQuery, null);

        try {
            while (cursor.moveToNext()) {
                int no = cursor.getInt(0);
                // TEXT로 선언된 "id" 컬럼 값 가져오기.
                String id = cursor.getString(1);
                // TEXT로 선언된 "pass" 컬럼 값 가져오기.
                String pass = cursor.getString(2);
                // TEXT로 선언된 "alarm" 컬럼 값 가져오기.
                String nickname = cursor.getString(3);
                // TEXT로 선언된 "nickname" 컬럼 값 가져오기.
                String alarm = cursor.getString(4);

                temp[0] = id;
                temp[1] = pass;
                temp[2] = nickname;
                temp[3] = alarm;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
        return temp;
    }

    public void updateAlarm(String tableName, String alarm) {
        try {
            SQLiteDatabase db = getReadableDatabase();
            String query = "UPDATE " + tableName + " SET ALARM = '" + alarm + "' WHERE _NO = 1";
            db.execSQL(query);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void delete(String tableName) {
        try {
            SQLiteDatabase db = getReadableDatabase();
            String query = "DELETE FROM " + tableName;
            db.execSQL(query);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

/*    public void insert_story(String auto, String TABLE_NAME) {
        try {
            SQLiteDatabase db = getReadableDatabase();
            int row = getProfilesCount(TABLE_NAME);
            if (row != 0) {
                String[] test = select_story("STORY");
                String sqlDelete = "DELETE FROM " + TABLE_NAME;
                db.execSQL(sqlDelete);
                String sqlInsert = "INSERT INTO " + TABLE_NAME + "(AUTO_PLAY) VALUES ('" + test[0] + "')";
                db.execSQL(sqlInsert);
            } else {
                String sqlInsert = "INSERT INTO " + TABLE_NAME + "(AUTO_PLAY) VALUES ('" + auto + "')";
                db.execSQL(sqlInsert);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update_story(String auto, String TABLE_NAME) {
        try {
            SQLiteDatabase db = getReadableDatabase();
            int row = getProfilesCount(TABLE_NAME);
            if (row != 0) {
                String sqlInsert = "UPDATE " + TABLE_NAME + " SET AUTO_PLAY = '" + auto + "'";
                db.execSQL(sqlInsert);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String[] select_story(String tableName) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String[] temp = new String[4];
            String idQuery = "SELECT * FROM " + tableName;

            cursor = db.rawQuery(idQuery, null);
            while (cursor.moveToNext()) {

                // TEXT로 선언된 "auto_play" 컬럼 값 가져오기.
                String auto = cursor.getString(0);

                temp[0] = auto;
                Log.d("select_database", temp[0]);

            }
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();

        }

        return null;
    }*/

}


