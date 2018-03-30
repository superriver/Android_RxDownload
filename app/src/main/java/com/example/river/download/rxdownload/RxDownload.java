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


//    //开始下载
//    public Observable<FileInfo> start() {
//        return start();
//    }

//
//    //暂停
//    public Observable<FileInfo> pause() {
//
//    }
//
//    //取消
//    public Observable<FileInfo> cancel() {
//
//    }
//
//    //重新开始
//    public Observable<FileInfo> restart() {
//
//    }
}
