package com.example.river.download.rxdownload;

import android.content.Context;

import java.util.List;

import io.reactivex.Observable;


/**
 * Created by Administrator on 2018/3/29.
 */

public class RxDownload {
    private static final RxDownload mDownload = new RxDownload();

    private DownloadManager manager;
    private String url;
    private Context mContext;
    private DBOption mOption;

    private RxDownload() {
        mOption = DBOption.getSingleton(mContext);
        manager = new DownloadManager();
    }

    public static RxDownload of() {
        return mDownload;
    }

    public RxDownload with(Context context) {
        mContext = context;
        return this;
    }

    public RxDownload url(String url) {
        this.url = url;
        return this;
    }


    public void start(DownloadListener listener) {
        manager.start(url, listener);
    }

    public void pause(DownloadRecord record) {
        manager.pause(record.getUrl());
        mOption.insert(record);
    }

    public void cancel(DownloadRecord record) {
        manager.cancel(record.getUrl());
    }

    public Observable<DownloadRecord> getDownloadRecordFromDB(String url) {
        return mOption.readRecord(url);
    }

    public Observable<List<DownloadRecord>> getAllDownloadRecordFromDB() {
        return mOption.readAllRecords();
    }
}
