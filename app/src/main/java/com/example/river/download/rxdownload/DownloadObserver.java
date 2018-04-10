package com.example.river.download.rxdownload;

import android.util.Log;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by Administrator on 2018/3/29.
 */

public class DownloadObserver extends DisposableObserver<DownloadRecord> {

    private DownloadListener listener;

    public DownloadObserver(DownloadListener listener) {
        this.listener = listener;
        event = new DownloadEvent();
    }

    protected String errMsg = "";
    protected Disposable disposable;
    private DownloadRecord mRecord;
    private DownloadEvent event;



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onNext(DownloadRecord downloadRecord) {
        Log.d("huang", "onNext");
        listener.onProgress(downloadRecord);
        mRecord = downloadRecord;
    }


    @Override
    public void onError(Throwable e) {
        Log.d("huang", "onError--" + e.getMessage());
        listener.onFailed(mRecord);
        //mOption.insert(mRecord);

    }

    @Override
    public void onComplete() {
        listener.onSuccess(mRecord);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }



}
