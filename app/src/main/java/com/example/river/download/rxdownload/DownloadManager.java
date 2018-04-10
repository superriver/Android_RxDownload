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
    private Map<String, DownloadObserver> mSubMap;
    private Map<String, DownloadRecord> mRecordMap;
    //  private Map<String, Disposable> mDisposableMap;
    private Map<String, Call> mCallMap;

    private String mFileName;

    //private CompositeDisposable disposable = new CompositeDisposable();

    public DownloadManager() {
        client = new OkHttpClient();
        mSubMap = new HashMap<>();
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
                .map(new Function<String, DownloadRecord>() {

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
                        String fileName = mFileName == null ? s.substring(s.lastIndexOf("/")) : mFileName;
                        record.setFileName(fileName);
                        return record;
                    }
                }).flatMap(new Function<DownloadRecord, ObservableSource<DownloadRecord>>() {
                    @Override
                    public ObservableSource<DownloadRecord> apply(@NonNull DownloadRecord record) throws Exception {
                        return Observable.create(new DownloadSubscriber(record, mCallMap));
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

    }

    public void start(String url, DownloadListener listenerr) {
        DownloadObserver downloadObserver = new DownloadObserver(listenerr);
        mSubMap.put(url, downloadObserver);
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

//    public void isExit() {
//        Observable<DownloadRecord> db = Observable.create(new ObservableOnSubscribe<DownloadRecord>() {
//            @Override
//            public void subscribe(ObservableEmitter<DownloadRecord> e) throws Exception {
//                if (mOption.isExit(mUrl)) {
//                    mOption.readRecord(mUrl);
//                } else {
//
//                }
//            }
//        });
//    }

//    public DownloadRecord getRealFileName(DownloadRecord record) {
//        String fileName = record.getFileName();
//        long finishedLen = 0, totalLen = record.getTotalSize();
//        File file = new File(App.getContext().getFilesDir(), fileName);
//        if (file.exists()) {
//            finishedLen = file.length();
//        }
//        int i = 1;
//        while (finishedLen >= totalLen) {
//            int dotIndex = fileName.lastIndexOf(".");
//            String newFileName;
//            if (dotIndex == -1) {
//                newFileName = fileName + "(" + i + ")";
//            } else {
//                newFileName = fileName.substring(0, dotIndex)
//                        + "(" + i + ")" + fileName.substring(dotIndex);
//            }
//            File newFile = new File(App.getContext().getFilesDir(), newFileName);
//            file = newFile;
//            finishedLen = newFile.length();
//            i++;
//        }
//        record.setProgress(finishedLen);
//        record.setFileName(file.getName());
//        return record;
//    }


}
