package com.example.river.download.rxdownload;

/**
 * Created by Administrator on 2018/3/29.
 */

public class RxDownload {
    private static final RxDownload download = new RxDownload();

    private RxManager manager;

    private RxDownload() {

    }

    public static RxDownload of() {
        return download;
    }

    public RxDownload url(String url) {
        manager = new RxManager(url);
        return this;
    }


    public void start(DownloadObserver observer) {
        manager.start(observer);
    }


}
