package com.example.river.download.rxdownload;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/3/29.
 */

public class DownloadManager {
    public static final int NORMAL_STATE = 1000;
    public static final int START_STATE = 1001;
    public static final int PAUSE_STATE = 1002;
    public static final int CANCEL_STATE = 1003;
    public static final int COMPLETE_STATE = 1004;
    private OkHttpClient client;
    private Map<String, DownloadRecord> mRecordMap;
    private Map<String, Call> mCallMap;
    //private CompositeDisposable disposable = new CompositeDisposable();

    public DownloadManager() {
        client = new OkHttpClient();
        mRecordMap = new HashMap<>();
        mCallMap = new HashMap<>();
    }


    //获取文件大总长度
    public long getContentLength(String url) {
        //  Observable.just(mUrl).
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request)
                    .execute();
            if (response != null && response.isSuccessful()) {
                return response.body().contentLength();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Observable<DownloadRecord> getObservable(String url) {
        return Observable.just(url)
                .map(new Function<String, DownloadRecord>() {//转换数据类型

                    @Override
                    public DownloadRecord apply(@NonNull String s) throws Exception {
                        long totalSize = getContentLength(s);
                        DownloadRecord record = mRecordMap.get(s);
                        if (record == null) {
                            record = new DownloadRecord();
                            mRecordMap.put(s, record);
                        }
                        record.setUrl(s);
                        record.setTotalSize(totalSize);
                        String fileName = s.substring(s.lastIndexOf("/"));
                        record.setFileName(fileName);
                        return record;
                    }
                }).flatMap(new Function<DownloadRecord, ObservableSource<DownloadRecord>>() {//
                    @Override
                    public ObservableSource<DownloadRecord> apply(@NonNull DownloadRecord record) throws Exception {
                        return Observable.create(new DownloadSubscriber(record, mCallMap));
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

    }

    public void start(String url, DownloadListener listener) {
        DownloadObserver downloadObserver = new DownloadObserver(listener);
        getObservable(url)
                .subscribeWith(downloadObserver);
    }

    public void pause(String url) {
        Call call = mCallMap.get(url);
        if (call != null) {
            call.cancel();
        }
        mCallMap.remove(url);
    }

    public void cancel(String url) {
        mCallMap.remove(url);
    }


}
