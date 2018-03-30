package com.example.river.download.rxdownload;

import com.example.river.download.FileInfo;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2018/3/29.
 */

public abstract class DownloadObserver implements Observer<FileInfo> {
    private Disposable d;
    private FileInfo fileInfo;

    @Override
    public void onSubscribe(Disposable d) {
        this.d = d;
    }

    @Override
    public void onNext(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

    @Override
    public void onError(Throwable e) {

    }
}
