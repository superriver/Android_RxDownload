package com.example.river.download.rxdownload;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/3/29.
 */

public class RxManager {
    private OkHttpClient client;
    private String url;
    private Map<String, Call> map;

    public RxManager(String url) {
        this.url = url;
        client = new OkHttpClient();
        map = new HashMap<>();
    }

    public DownloadRecord newTask(String url) {
        DownloadRecord fileInfo = new DownloadRecord();
        fileInfo.setUrl(url);
        fileInfo.setTotalSize(getContentLength());
        String fileName = url.substring(url.lastIndexOf("/"));
        fileInfo.setFileName(fileName);
        return fileInfo;
    }

    public long getContentLength() {
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

    public void start(DownloadObserver downloadObserver) {
        Observable.just(url)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(@NonNull String s) throws Exception {
                        return !map.containsKey(s);
                    }
                })
                .flatMap(new Function<String, ObservableSource<DownloadRecord>>() {
                    @Override
                    public ObservableSource<DownloadRecord> apply(@NonNull String s) throws Exception {
                        return Observable.just(newTask(s));
                    }
                }).map(new Function<DownloadRecord, DownloadRecord>() {
            @Override
            public DownloadRecord apply(@NonNull DownloadRecord file) throws Exception {
                return getRealFileName(file);
            }
        }).flatMap(new Function<DownloadRecord, ObservableSource<DownloadRecord>>() {
            @Override
            public ObservableSource<DownloadRecord> apply(@NonNull DownloadRecord record) throws Exception {
                return Observable.create(new DownloadSubscriber(record, map));
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(downloadObserver);
    }

    public DownloadRecord getRealFileName(DownloadRecord record) {
        String fileName = record.getFileName();
        long finishedLen = 0, totalLen = record.getTotalSize();
        File file = new File(App.getContext().getFilesDir(), fileName);
        if (file.exists()) {
            finishedLen = file.length();
        }
        int i = 1;
        while (finishedLen >= totalLen) {
            int dotIndex = fileName.lastIndexOf(".");
            String newFileName;
            if (dotIndex == -1) {
                newFileName = fileName + "(" + i + ")";
            } else {
                newFileName = fileName.substring(0, dotIndex)
                        + "(" + i + ")" + fileName.substring(dotIndex);
            }
            File newFile = new File(App.getContext().getFilesDir(), newFileName);
            file = newFile;
            finishedLen = newFile.length();
            i++;
        }
        record.setProgress(finishedLen);
        record.setFileName(file.getName());
        return record;
    }
}
