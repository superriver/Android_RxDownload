package com.example.river.download.rxdownload;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by river on 2018/3/31.
 */

public class DBOption {

    private DBHelper dbHelper;
    private SQLiteDatabase readDB;
    private SQLiteDatabase writeDB;
    private final Object dblock = new Object();

    public DBOption(Context context) {
        dbHelper = new DBHelper(context);
    }

    public long insert(DownloadRecord record) {
        ContentValues cv = new ContentValues();
        cv.put(DB.FILE_NAME, record.getFileName());
        cv.put(DB.FILE_URL, record.getUrl());
        cv.put(DB.FILE_TOTAL_SIZE, record.getTotalSize());
        // cv.put(DB.FILE_SAVE_PATH, info.getLen());
        cv.put(DB.FILE_DOWNLOAD_SIZE, record.getProgress());
        // cv.put("finished",info.getFinished());
        return getWritableDatabase().insert(DB.TABLE_NAME, null, cv);
    }

    public long delete(String url) {
        return getReadableDatabase().delete(DB.TABLE_NAME, "url = ?", new String[]{url});
    }

    public DownloadRecord queryData(String url) {
        Cursor cursor = getReadableDatabase().query(true, DB.TABLE_NAME, null, "url=?", new String[]{url}, null, null, null, null);
        DownloadRecord info = new DownloadRecord();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                info.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
                info.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                info.setTotalSize(cursor.getInt(cursor.getColumnIndex("length")));
                //info.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
            }
            cursor.close();
        }
        return info;
    }

    //判断数据库中是否存在已下载的
    public boolean isExit(String url) {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(true, DB.TABLE_NAME, null, "url=?", new String[]{url}, null, null, null, null);
            return cursor.getCount() == 0;
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
    }

    public Observable<List<DownloadRecord>> readAllRecords() {
        return Observable.create(new ObservableOnSubscribe<List<DownloadRecord>>() {
            @Override
            public void subscribe(ObservableEmitter<List<DownloadRecord>> e) throws Exception {
                Cursor cursor = null;
                try {
                    cursor = getReadableDatabase().rawQuery("select * from " + DB.TABLE_NAME, new String[]{});
                    List<DownloadRecord> records = new ArrayList<>();
                    while (cursor.moveToNext()) {
                        DownloadRecord record = new DownloadRecord();
                        record.setFileName(cursor.getString(cursor.getColumnIndex(DB.FILE_NAME)));
                        record.setUrl(cursor.getString(cursor.getColumnIndex(DB.FILE_URL)));
                        record.setTotalSize(cursor.getInt(cursor.getColumnIndex(DB.FILE_TOTAL_SIZE)));
                        record.setProgress(cursor.getInt(cursor.getColumnIndex(DB.FILE_DOWNLOAD_SIZE)));
                        records.add(record);
                    }
                    e.onNext(records);
                    e.onComplete();
                } finally {
                    if (null != cursor) {
                        cursor.close();
                    }

                }


            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db = writeDB;
        if (db == null) {
            synchronized (dblock) {
                db = writeDB;
                if (db == null) {
                    db = writeDB = dbHelper.getWritableDatabase();
                }
            }
        }
        return db;
    }

    public SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase db = readDB;
        if (db == null) {
            synchronized (dblock) {
                db = readDB;
                if (db == null) {
                    db = readDB = dbHelper.getReadableDatabase();
                }
            }
        }
        return db;
    }

    public void closeDB() {
        if (dbHelper != null) {
            dbHelper.close();
            writeDB = null;
            readDB = null;
        }
    }
}
