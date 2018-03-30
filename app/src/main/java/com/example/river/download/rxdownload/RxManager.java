package com.example.river.download.rxdownload;

import com.example.river.download.FileInfo;

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

    public FileInfo newTask(String url) {
        FileInfo fileInfo = new FileInfo();
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
                .flatMap(new Function<String, ObservableSource<FileInfo>>() {
                    @Override
                    public ObservableSource<FileInfo> apply(@NonNull String s) throws Exception {
                        return Observable.just(newTask(s));
                    }
                }).map(new Function<FileInfo, FileInfo>() {
            @Override
            public FileInfo apply(@NonNull FileInfo file) throws Exception {
                return getRealFileName(file);
            }
        }).flatMap(new Function<FileInfo, ObservableSource<FileInfo>>() {
            @Override
            public ObservableSource<FileInfo> apply(@NonNull FileInfo fileInfo) throws Exception {
                return Observable.create(new DownloadSubscriber(fileInfo,map));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(downloadObserver);
    }

    public FileInfo getRealFileName(FileInfo fileInfo) {
        String fileName = fileInfo.getFileName();
        long finishedLen = 0, totalLen = fileInfo.getLen();
        File file = new File("", fileName);
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
            File newFile = new File("", newFileName);
            file = newFile;
            finishedLen = newFile.length();
            i++;
        }
        fileInfo.setProgress(finishedLen);
        fileInfo.setFileName(file.getName());
        return fileInfo;
    }
}
