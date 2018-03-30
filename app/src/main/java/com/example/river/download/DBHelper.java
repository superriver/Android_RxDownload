package com.example.river.download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/10/19.
 */

public class DBHelper extends SQLiteOpenHelper{

    public static  String TABLE_NAME = "file";
    public DBHelper(Context context) {
        super(context, "download.db", null, 1);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table file (_id integer primary key autoincrement ,fileName varchar,url varchar,length integer,finished integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void insert(SQLiteDatabase db, FileInfo info){
        try {
            ContentValues cv = new ContentValues();
            cv.put("fileName",info.getFileName());
            cv.put("url",info.getUrl());
            cv.put("length",info.getLen());
           // cv.put("finished",info.getFinished());
            db.insert(TABLE_NAME,null,cv);
        }catch (Exception e){
        }

    }

    public void delete(SQLiteDatabase db,String url){
        db.delete(TABLE_NAME,"url = ?",new String[]{url});
    }
    public FileInfo queryData(SQLiteDatabase db, String url){
        Cursor cursor = db.query(true,TABLE_NAME,null,"url=?",new String[]{url},null,null,null,null);
        FileInfo info = new FileInfo();
        if(cursor!=null){
            while (cursor.moveToNext()){
                info.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
                info.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                info.setLen(cursor.getInt(cursor.getColumnIndex("length")));
                //info.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
            }
            cursor.close();
        }
        return info;
    }
}

