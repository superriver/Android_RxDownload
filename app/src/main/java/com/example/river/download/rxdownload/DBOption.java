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

    private DBOption(Context context) {
        dbHelper = new DBHelper(context);
    }

    private volatile static DBOption singleton;

    public static DBOption getSingleton(Context context) {
        if (singleton == null) {
            synchronized (DBOption.class) {
                if (singleton == null) {
                    singleton = new DBOption(context);
                }
            }
        }
        return singleton;
    }

    public long insert(DownloadRecord record) {
        ContentValues cv = new ContentValues();
        cv.put(DB.FILE_NAME, record.getFileName());
        cv.put(DB.FILE_URL, record.getUrl());
        cv.put(DB.FILE_TOTAL_SIZE, record.getTotalSize());
        // cv.put(DB.FILE_SAVE_PATH, info.getLen());
        cv.put(DB.FILE_DOWNLOAD_SIZE, record.getProgress());
        cv.put(DB.FILE_STATE, record.isFinished());
        return getWritableDatabase().insert(DB.TABLE_NAME, null, cv);
    }

    public long updateRecord(String url, DownloadRecord record) {
        ContentValues values = new ContentValues();
        values.put(DB.FILE_TOTAL_SIZE, record.getTotalSize());
        values.put(DB.FILE_DOWNLOAD_SIZE, record.getProgress());
        return getWritableDatabase().update(DB.TABLE_NAME, values, "url=?", new String[]{url});
    }

    public long delete(String url) {
        return getReadableDatabase().delete(DB.TABLE_NAME, "url = ?", new String[]{url});
    }

    public DownloadRecord readSingleRecord(String url) {
        DownloadRecord info = new DownloadRecord();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery("select * from " + DB.TABLE_NAME + " where = " + "url=?", new String[]{url});
            DownloadRecord record = new DownloadRecord();
            while (cursor.moveToFirst()) {
                record.setFileName(cursor.getString(cursor.getColumnIndex(DB.FILE_NAME)));
                record.setUrl(cursor.getString(cursor.getColumnIndex(DB.FILE_URL)));
                record.setTotalSize(cursor.getInt(cursor.getColumnIndex(DB.FILE_TOTAL_SIZE)));
                record.setProgress(cursor.getInt(cursor.getColumnIndex(DB.FILE_DOWNLOAD_SIZE)));
            }

        } finally {
            if (null != cursor) {
                cursor.close();
            }

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

    public Observable<DownloadRecord> readRecord(final String url) {
        return Observable.create(new ObservableOnSubscribe<DownloadRecord>() {
            @Override
            public void subscribe(ObservableEmitter<DownloadRecord> e) throws Exception {
                Cursor cursor = null;
                try {
                    cursor = getReadableDatabase().rawQuery("select * from " + DB.TABLE_NAME + " where = " + "url=?", new String[]{url});
                    DownloadRecord record = new DownloadRecord();
                    while (cursor.moveToNext()) {
                        record.setFileName(cursor.getString(cursor.getColumnIndex(DB.FILE_NAME)));
                        record.setUrl(cursor.getString(cursor.getColumnIndex(DB.FILE_URL)));
                        record.setTotalSize(cursor.getInt(cursor.getColumnIndex(DB.FILE_TOTAL_SIZE)));
                        record.setProgress(cursor.getInt(cursor.getColumnIndex(DB.FILE_DOWNLOAD_SIZE)));
                        e.onNext(record);
                    }

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
