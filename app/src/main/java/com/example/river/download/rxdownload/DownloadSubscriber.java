package com.example.river.download.rxdownload;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Map;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/3/30.
 * 主题对象
 */


public class DownloadSubscriber implements ObservableOnSubscribe<DownloadRecord> {
    private DownloadRecord record;
    private OkHttpClient client;
    private Map<String, Call> map;

    public DownloadSubscriber(DownloadRecord record, Map<String, Call> map) {
        this.record = record;
        this.map = map;
        client = new OkHttpClient();
    }

    @Override
    public void subscribe(ObservableEmitter<DownloadRecord> e) throws Exception {

        long finishedLen = record.getProgress();
        long totalLen = record.getTotalSize();
        String url = record.getUrl();
        e.onNext(record);//初始进度信息
        Request request = new Request.Builder()
                .addHeader("RANGE", "bytes=" + finishedLen + "-" + totalLen)
                .url(url)
                .build();
        Call call = client.newCall(request);
        map.put(url, call);
        Response response = call.execute();
        File file = new File(Constant.DEFAULT_FILE_PATH, record.getFileName());
        InputStream is = null;
        RandomAccessFile randomAccessFile = null;
        try {
            is = response.body().byteStream();
            randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(finishedLen);//跳过已经下载的字节
            byte[] buffer = new byte[2048];
            int len;
            while ((len = is.read(buffer)) != -1) {
                randomAccessFile.write(buffer, 0, len);
                finishedLen += len;
                record.setProgress(finishedLen);
                e.onNext(record);//发射下载的进度
            }
            map.remove(url);
        } finally {
            if (is != null) {
                is.close();
            }
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }

        }
        e.onComplete();
    }

}
